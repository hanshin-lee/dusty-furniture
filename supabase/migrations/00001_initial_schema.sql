-- Enable UUID generation
create extension if not exists "uuid-ossp";

-- ============================================================
-- USERS (extends Supabase auth.users)
-- ============================================================
create type user_role as enum ('buyer', 'seller', 'admin');

create table public.profiles (
    id uuid primary key references auth.users(id) on delete cascade,
    email text not null,
    display_name text not null default '',
    avatar_url text,
    role user_role not null default 'buyer',
    phone text,
    address_line1 text,
    address_line2 text,
    city text,
    state text,
    zip_code text,
    country text default 'US',
    seller_verified boolean not null default false,
    seller_bio text,
    created_at timestamptz not null default now(),
    updated_at timestamptz not null default now()
);

-- Auto-create profile on signup
create or replace function public.handle_new_user()
returns trigger as $$
begin
    insert into public.profiles (id, email, display_name)
    values (new.id, new.email, coalesce(new.raw_user_meta_data->>'display_name', ''));
    return new;
end;
$$ language plpgsql security definer;

create trigger on_auth_user_created
    after insert on auth.users
    for each row execute function public.handle_new_user();

-- ============================================================
-- CATEGORIES
-- ============================================================
create table public.categories (
    id uuid primary key default uuid_generate_v4(),
    name text not null unique,
    slug text not null unique,
    description text,
    icon_url text,
    display_order int not null default 0,
    created_at timestamptz not null default now()
);

-- ============================================================
-- LISTINGS
-- ============================================================
create type listing_condition as enum (
    'mint', 'excellent', 'good', 'fair', 'poor', 'for_parts'
);
create type listing_status as enum (
    'draft', 'pending_review', 'active', 'sold', 'archived'
);

create table public.listings (
    id uuid primary key default uuid_generate_v4(),
    seller_id uuid not null references public.profiles(id) on delete cascade,
    category_id uuid not null references public.categories(id),
    title text not null,
    description text not null,
    price decimal(10,2) not null check (price >= 0),
    condition listing_condition not null,
    status listing_status not null default 'pending_review',
    era text,
    material text,
    dimensions_cm jsonb,
    weight_kg decimal(6,2),
    location_city text,
    location_state text,
    is_featured boolean not null default false,
    views_count int not null default 0,
    images text[] not null default '{}',
    thumbnail_url text,
    created_at timestamptz not null default now(),
    updated_at timestamptz not null default now()
);

create index idx_listings_category on public.listings(category_id);
create index idx_listings_seller on public.listings(seller_id);
create index idx_listings_status on public.listings(status);
create index idx_listings_price on public.listings(price);
create index idx_listings_created on public.listings(created_at desc);

-- Full-text search
alter table public.listings add column fts tsvector
    generated always as (
        setweight(to_tsvector('english', coalesce(title, '')), 'A') ||
        setweight(to_tsvector('english', coalesce(description, '')), 'B') ||
        setweight(to_tsvector('english', coalesce(era, '')), 'C') ||
        setweight(to_tsvector('english', coalesce(material, '')), 'C')
    ) stored;
create index idx_listings_fts on public.listings using gin(fts);

-- ============================================================
-- CART
-- ============================================================
create table public.cart_items (
    id uuid primary key default uuid_generate_v4(),
    user_id uuid not null references public.profiles(id) on delete cascade,
    listing_id uuid not null references public.listings(id) on delete cascade,
    added_at timestamptz not null default now(),
    unique(user_id, listing_id)
);

-- ============================================================
-- ORDERS
-- ============================================================
create type order_status as enum (
    'pending', 'confirmed', 'shipped', 'delivered', 'cancelled', 'refunded'
);

create table public.orders (
    id uuid primary key default uuid_generate_v4(),
    buyer_id uuid not null references public.profiles(id),
    total_amount decimal(10,2) not null,
    status order_status not null default 'pending',
    shipping_address jsonb not null,
    payment_intent_id text,
    notes text,
    created_at timestamptz not null default now(),
    updated_at timestamptz not null default now()
);

create table public.order_items (
    id uuid primary key default uuid_generate_v4(),
    order_id uuid not null references public.orders(id) on delete cascade,
    listing_id uuid not null references public.listings(id),
    price_at_purchase decimal(10,2) not null,
    seller_id uuid not null references public.profiles(id)
);

-- ============================================================
-- REVIEWS
-- ============================================================
create table public.reviews (
    id uuid primary key default uuid_generate_v4(),
    listing_id uuid not null references public.listings(id) on delete cascade,
    reviewer_id uuid not null references public.profiles(id),
    rating int not null check (rating >= 1 and rating <= 5),
    comment text,
    created_at timestamptz not null default now(),
    unique(listing_id, reviewer_id)
);

-- ============================================================
-- ROW LEVEL SECURITY
-- ============================================================
alter table public.profiles enable row level security;
alter table public.categories enable row level security;
alter table public.listings enable row level security;
alter table public.cart_items enable row level security;
alter table public.orders enable row level security;
alter table public.order_items enable row level security;
alter table public.reviews enable row level security;

-- Profiles
create policy "Profiles are viewable by everyone" on public.profiles
    for select using (true);
create policy "Users can update own profile" on public.profiles
    for update using (auth.uid() = id);

-- Categories
create policy "Categories are viewable by everyone" on public.categories
    for select using (true);
create policy "Admins can insert categories" on public.categories
    for insert with check (
        exists (select 1 from public.profiles where id = auth.uid() and role = 'admin')
    );
create policy "Admins can update categories" on public.categories
    for update using (
        exists (select 1 from public.profiles where id = auth.uid() and role = 'admin')
    );
create policy "Admins can delete categories" on public.categories
    for delete using (
        exists (select 1 from public.profiles where id = auth.uid() and role = 'admin')
    );

-- Listings
create policy "Active listings are public" on public.listings
    for select using (
        status = 'active' or seller_id = auth.uid() or
        exists (select 1 from public.profiles where id = auth.uid() and role = 'admin')
    );
create policy "Sellers can create listings" on public.listings
    for insert with check (
        seller_id = auth.uid() and
        exists (select 1 from public.profiles where id = auth.uid() and (role = 'seller' or role = 'admin'))
    );
create policy "Sellers can update own listings" on public.listings
    for update using (
        seller_id = auth.uid() or
        exists (select 1 from public.profiles where id = auth.uid() and role = 'admin')
    );

-- Cart
create policy "Users manage own cart" on public.cart_items
    for all using (user_id = auth.uid());

-- Orders
create policy "Users see own orders" on public.orders
    for select using (
        buyer_id = auth.uid() or
        exists (select 1 from public.profiles where id = auth.uid() and role = 'admin')
    );
create policy "Users can create orders" on public.orders
    for insert with check (buyer_id = auth.uid());

-- Order items
create policy "Order items visible with order" on public.order_items
    for select using (
        exists (
            select 1 from public.orders
            where id = order_id and (
                buyer_id = auth.uid() or seller_id = auth.uid() or
                exists (select 1 from public.profiles where id = auth.uid() and role = 'admin')
            )
        )
    );
create policy "Order items created with order" on public.order_items
    for insert with check (
        exists (select 1 from public.orders where id = order_id and buyer_id = auth.uid())
    );

-- Reviews
create policy "Reviews are public" on public.reviews
    for select using (true);
create policy "Authenticated users can review" on public.reviews
    for insert with check (auth.uid() = reviewer_id);
