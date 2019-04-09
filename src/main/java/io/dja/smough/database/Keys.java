/*
 * This file is generated by jOOQ.
 */
package io.dja.smough.database;


import io.dja.smough.database.tables.Category;
import io.dja.smough.database.tables.FlywaySchemaHistory;
import io.dja.smough.database.tables.Post;
import io.dja.smough.database.tables.records.CategoryRecord;
import io.dja.smough.database.tables.records.FlywaySchemaHistoryRecord;
import io.dja.smough.database.tables.records.PostRecord;

import javax.annotation.Generated;

import org.jooq.ForeignKey;
import org.jooq.Identity;
import org.jooq.UniqueKey;
import org.jooq.impl.Internal;


/**
 * A class modelling foreign key relationships and constraints of tables of 
 * the <code>public</code> schema.
 */
@Generated(
    value = {
        "http://www.jooq.org",
        "jOOQ version:3.11.11"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class Keys {

    // -------------------------------------------------------------------------
    // IDENTITY definitions
    // -------------------------------------------------------------------------

    public static final Identity<CategoryRecord, Integer> IDENTITY_CATEGORY = Identities0.IDENTITY_CATEGORY;
    public static final Identity<PostRecord, Integer> IDENTITY_POST = Identities0.IDENTITY_POST;

    // -------------------------------------------------------------------------
    // UNIQUE and PRIMARY KEY definitions
    // -------------------------------------------------------------------------

    public static final UniqueKey<CategoryRecord> CATEGORY_PKEY = UniqueKeys0.CATEGORY_PKEY;
    public static final UniqueKey<FlywaySchemaHistoryRecord> FLYWAY_SCHEMA_HISTORY_PK = UniqueKeys0.FLYWAY_SCHEMA_HISTORY_PK;
    public static final UniqueKey<PostRecord> POST_PKEY = UniqueKeys0.POST_PKEY;

    // -------------------------------------------------------------------------
    // FOREIGN KEY definitions
    // -------------------------------------------------------------------------

    public static final ForeignKey<PostRecord, CategoryRecord> POST__POST_CATEGORY_FKEY = ForeignKeys0.POST__POST_CATEGORY_FKEY;

    // -------------------------------------------------------------------------
    // [#1459] distribute members to avoid static initialisers > 64kb
    // -------------------------------------------------------------------------

    private static class Identities0 {
        public static Identity<CategoryRecord, Integer> IDENTITY_CATEGORY = Internal.createIdentity(Category.CATEGORY, Category.CATEGORY.ID);
        public static Identity<PostRecord, Integer> IDENTITY_POST = Internal.createIdentity(Post.POST, Post.POST.ID);
    }

    private static class UniqueKeys0 {
        public static final UniqueKey<CategoryRecord> CATEGORY_PKEY = Internal.createUniqueKey(Category.CATEGORY, "category_pkey", Category.CATEGORY.ID);
        public static final UniqueKey<FlywaySchemaHistoryRecord> FLYWAY_SCHEMA_HISTORY_PK = Internal.createUniqueKey(FlywaySchemaHistory.FLYWAY_SCHEMA_HISTORY, "flyway_schema_history_pk", FlywaySchemaHistory.FLYWAY_SCHEMA_HISTORY.INSTALLED_RANK);
        public static final UniqueKey<PostRecord> POST_PKEY = Internal.createUniqueKey(Post.POST, "post_pkey", Post.POST.ID);
    }

    private static class ForeignKeys0 {
        public static final ForeignKey<PostRecord, CategoryRecord> POST__POST_CATEGORY_FKEY = Internal.createForeignKey(io.dja.smough.database.Keys.CATEGORY_PKEY, Post.POST, "post__post_category_fkey", Post.POST.CATEGORY);
    }
}