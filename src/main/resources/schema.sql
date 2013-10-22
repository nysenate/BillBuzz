DROP TABLE IF EXISTS billbuzz_thread;
CREATE TABLE billbuzz_thread (
    id varchar(255) PRIMARY KEY,
    forumId varchar(255),
    authorId varchar(255),
    
    billId varchar(255),
    sponsor varchar(255),
    
    isDeleted boolean,
    isClosed boolean,
    userSubscription boolean,

    link text,
    slug text,
    title text,
    message text,
    feed varchar(255),
    category varchar(255),

    posts int(11),
    likes int(11),
    dislikes int(11),
    reactions int(11),
    userScore int(11),

    createdAt timestamp,
    updatedAt timestamp
);

DROP TABLE IF EXISTS billbuzz_post;
CREATE TABLE billbuzz_post (
    id varchar(255) PRIMARY KEY,
    forumId varchar(255),
    threadId varchar(255),
    authorId varchar(255),
    parentId varchar(255),
    
    juliaFlagged boolean,
    isFlagged boolean,
    isDeleted boolean,
    isHighlighted boolean,
    isEdited boolean,
    isApproved boolean,
    isSpam boolean,
    
    rawMessage text,
    message text,

    points int(11),
    likes int(11),
    dislikes int(11),
    userScore int(11),
    numReports int(11),

    createdAt timestamp,
    updatedAt timestamp
);

DROP TABLE IF EXISTS billbuzz_author;
CREATE TABLE billbuzz_author (
    id varchar(255) PRIMARY KEY,
    username varchar(255),
    name varchar(255),
    url varchar(255),
    avatarUrl varchar(255),
    profileUrl varchar(255),
    emailHash varchar(255),
    location varchar(255),

    about text,
    
    isPrimary boolean,
    isPrivate boolean,
    isAnonymous boolean,
    isFollowing boolean,
    isFollowedBy boolean,

    rep double,
    reputation double,
    joinedAt timestamp NULL DEFAULT NULL,
    updatedAt timestamp
);

DROP TABLE IF EXISTS billbuzz_senator;
CREATE TABLE billbuzz_senator (
    id int(11) AUTO_INCREMENT PRIMARY KEY,
    name varchar(255),
    shortName varchar(255),
    session int(11),
    UNIQUE KEY (shortname, session)
);

DROP TABLE IF EXISTS billbuzz_affiliation;
CREATE TABLE billbuzz_affiliation (
    senatorId int(11),
    partyId varchar(255),
    UNIQUE KEY (senatorId, partyId)
);

DROP TABLE IF EXISTS billbuzz_update;
CREATE TABLE billbuzz_update (
    id int(11) AUTO_INCREMENT PRIMARY KEY,
    createdAt timestamp,
    sentAt timestamp NULL DEFAULT NULL
);

DROP TABLE IF EXISTS billbuzz_approval;
CREATE TABLE billbuzz_approval (
    postId varchar(255) PRIMARY KEY,
    updateId int(11),
    threadId varchar(255),
    authorId varchar(255)
);

DROP TABLE IF EXISTS billbuzz_user;
CREATE TABLE billbuzz_user (
    id int(11) AUTO_INCREMENT PRIMARY KEY,
    email varchar(255),
    firstName varchar(255),
    lastName varchar(255),
    activated boolean,
    confirmedAt timestamp NULL,
    createdAt timestamp NOT NULL
);

DROP TABLE IF EXISTS billbuzz_confirmation;
CREATE TABLE billbuzz_confirmation (
    id int(11) AUTO_INCREMENT PRIMARY KEY,
    code varchar(255),
    action varchar(255),
    userId int,
    createdAt timestamp NOT NULL,
    expiresAt timestamp NULL DEFAULT NULL,
    usedAt timestamp NULL DEFAULT NULL
);

DROP TABLE IF EXISTS billbuzz_subscription;
CREATE TABLE billbuzz_subscription (
    id int(11) AUTO_INCREMENT PRIMARY KEY,
    userId int,
    category varchar(255),
    value varchar(255),
    createdAt timestamp NOT NULL
);