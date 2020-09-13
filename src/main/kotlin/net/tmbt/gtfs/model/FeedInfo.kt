package net.tmbt.gtfs.model

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable

object FeedInfoTable : IntIdTable() {
    val feedPublisherName = varchar("feed_publisher_name", MAX_IDENTIFIER_LENGTH)
    val feedPublisherUrl = varchar("feed_publisher_url", MAX_TEXT_LENGTH)
    val feedLanguage = varchar("feed_lang", MAX_IDENTIFIER_LENGTH)
    val defaultLanguage = varchar("default_lang", MAX_IDENTIFIER_LENGTH).nullable()
    val feedStartDate = varchar("feed_start_date", MAX_IDENTIFIER_LENGTH).nullable()
    val feedEndDate = varchar("feed_end_date", MAX_IDENTIFIER_LENGTH).nullable()
    val feedVersion = varchar("feed_version", MAX_IDENTIFIER_LENGTH).nullable()
    val feedContactEmail = varchar("feed_contact_email", MAX_IDENTIFIER_LENGTH).nullable()
    val feedContactUrl = varchar("feed_contact_url", MAX_TEXT_LENGTH).nullable()
}

class FeedInfo(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<FeedInfo>(FeedInfoTable)

    var feedPublisherName by FeedInfoTable.feedPublisherName
    var feedPublisherUrl by FeedInfoTable.feedPublisherUrl
    var feedLanguage by FeedInfoTable.feedLanguage
    var defaultLanguage by FeedInfoTable.defaultLanguage
    var feedStartDate by FeedInfoTable.feedStartDate
    var feedEndDate by FeedInfoTable.feedEndDate
    var feedVersion by FeedInfoTable.feedVersion
    var feedContactEmail by FeedInfoTable.feedContactEmail
    var feedContactUrl by FeedInfoTable.feedContactUrl
}