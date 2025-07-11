create schema if not exists doc_system;
create extension if not exists "uuid-ossp";
create table documents(
    id uuid primary key default uuid_generate_v4(),
    title varchar(255) not null,
    content text not null,
    created_at timestamp default current_timestamp,
    updated_at timestamp
);

create table comments(
    id uuid primary key,
    document_id uuid not null,
    text text not null,

    context_type varchar(50) not null,
    context_reference varchar(255) not null,
    created_at timestamp default current_timestamp,

    constraint fk_document foreign key (document_id) references documents(id) on delete cascade
);

--seed data
INSERT INTO documents (id, title, content)
VALUES (uuid_generate_v4(), 'Sample Doc', 'Paragraph 1.\nParagraph 2.\nParagraph 3.');