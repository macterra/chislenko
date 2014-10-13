#
# Table structure for table `banned`
#

CREATE TABLE banned (
  type tinytext NOT NULL,
  value tinytext NOT NULL
) TYPE=MyISAM;

#
# Table structure for table `boards`
#

CREATE TABLE boards (
  ID_CAT tinyint(4) NOT NULL default '0',
  ID_BOARD tinyint(4) NOT NULL auto_increment,
  name tinytext NOT NULL,
  description text,
  moderators text,
  boardOrder tinyint(4) NOT NULL default '0',
  numTopics int(11) NOT NULL default '0',
  numPosts int(11) NOT NULL default '0',
  PRIMARY KEY  (ID_BOARD)
) TYPE=MyISAM;

#
# Dumping data for table `boards`
#

INSERT INTO boards VALUES (1, 1, 'General Discussion', 'Feel free to talk about anything and everything in this board.', '', 0, 1, 1);
# --------------------------------------------------------

#
# Table structure for table `categories`
#

CREATE TABLE categories (
  ID_CAT tinyint(4) NOT NULL auto_increment,
  name tinytext NOT NULL,
  memberGroups text,
  catOrder tinyint(4) NOT NULL default '0',
  PRIMARY KEY  (ID_CAT)
) TYPE=MyISAM;

#
# Dumping data for table `categories`
#

INSERT INTO categories VALUES (1, 'General Category', '', 0);
# --------------------------------------------------------

#
# Table structure for table `censor`
#

CREATE TABLE censor (
  vulgar tinytext,
  proper tinytext
) TYPE=MyISAM;

#
# Table structure for table `instant_messages`
#

CREATE TABLE instant_messages (
  ID_IM bigint(20) NOT NULL auto_increment,
  ID_MEMBER_FROM int(11) NOT NULL default '0',
  ID_MEMBER_TO int(11) NOT NULL default '0',
  deletedBy int(11) NOT NULL default '-1',
  fromName tinytext NOT NULL,
  toName tinytext NOT NULL,
  msgtime bigint(20) default NULL,
  subject tinytext,
  body text,
  PRIMARY KEY  (ID_IM)
) TYPE=MyISAM;


#
# Table structure for table `log_banned`
#

CREATE TABLE log_banned (
  ip tinytext,
  email tinytext,
  logTime bigint(20) default NULL
) TYPE=MyISAM;


#
# Table structure for table `log_boards`
#

CREATE TABLE log_boards (
  memberName tinytext NOT NULL,
  ID_BOARD tinyint(4) NOT NULL default '0',
  logTime bigint(20) default NULL
) TYPE=MyISAM;


#
# Table structure for table `log_clicks`
#

CREATE TABLE log_clicks (
  ip tinytext,
  logTime bigint(20) NOT NULL default '0',
  agent tinytext,
  toUrl tinytext,
  fromUrl tinytext,
  KEY logTime (logTime)
) TYPE=MyISAM;

#
# Table structure for table `log_floodcontrol`
#

CREATE TABLE log_floodcontrol (
  ip tinytext,
  logTime bigint(20) default NULL
) TYPE=MyISAM;


#
# Table structure for table `log_karma`
#

CREATE TABLE log_karma (
  ID_TARGET int(11) NOT NULL default '0',
  ID_EXECUTOR int(11) NOT NULL default '0',
  action tinytext NOT NULL,
  logTime bigint(20) NOT NULL default '0'
) TYPE=MyISAM;

#
# Table structure for table `log_mark_read`
#

CREATE TABLE log_mark_read (
  memberName tinytext NOT NULL,
  ID_BOARD tinyint(4) NOT NULL default '0',
  logTime bigint(20) default NULL
) TYPE=MyISAM;

#
# Table structure for table `log_online`
#

CREATE TABLE log_online (
  identity tinytext NOT NULL,
  logTime bigint(20) default NULL
) TYPE=MyISAM;


#
# Table structure for table `log_topics`
#

CREATE TABLE log_topics (
  memberName tinytext NOT NULL,
  ID_TOPIC int(11) NOT NULL default '0',
  logTime bigint(20) default NULL
) TYPE=MyISAM;

#
# Table structure for table `membergroups`
#

CREATE TABLE membergroups (
  ID_GROUP tinyint(4) NOT NULL auto_increment,
  membergroup tinytext NOT NULL,
  grouptype tinyint(1) NOT NULL default '0',
  PRIMARY KEY  (ID_GROUP)
) TYPE=MyISAM;

#
# Dumping data for table `membergroups`
#

INSERT INTO membergroups VALUES (1, 'Administrator', 0);
INSERT INTO membergroups VALUES (2, 'Moderator', 0);
INSERT INTO membergroups VALUES (3, 'Newbie', 0);
INSERT INTO membergroups VALUES (4, 'Jr. Member', 0);
INSERT INTO membergroups VALUES (5, 'Full Member', 0);
INSERT INTO membergroups VALUES (6, 'Sr. Member', 0);
INSERT INTO membergroups VALUES (7, 'YaBB God', 0);
INSERT INTO membergroups VALUES (8, 'Global Moderator', 0);

# --------------------------------------------------------

#
# Table structure for table `members`
#

CREATE TABLE members (
  ID_MEMBER int(10) unsigned NOT NULL auto_increment,
  memberName tinytext NOT NULL,
  realName tinytext,
  passwd tinytext NOT NULL,
  emailAddress tinytext,
  dateRegistered bigint(20) default NULL,
  personalText tinytext,
  memberGroup tinytext,
  gender tinytext,
  birthdate date NOT NULL default '0000-00-00',
  websiteTitle tinytext,
  websiteUrl tinytext,
  location tinytext,
  ICQ tinytext,
  AIM tinytext,
  YIM tinytext,
  hideEmail tinyint(4) default NULL,
  timeFormat tinytext,
  signature text,
  posts int(11) default NULL,
  timeOffset float default NULL,
  avatar tinytext,
  im_ignore_list text,
  im_email_notify tinyint(4) NOT NULL default '0',
  karmaGood int NOT NULL default '0',
  karmaBad int NOT NULL default '0',
  lastLogin bigint(20),
  PRIMARY KEY  (ID_MEMBER),
  UNIQUE KEY memberID (ID_MEMBER),
  KEY memberID_2 (ID_MEMBER)
) TYPE=MyISAM;

#
# Table structure for table `messages`
#

CREATE TABLE messages (
  ID_MSG int(11) NOT NULL auto_increment,
  ID_TOPIC int(11) NOT NULL default '0',
  ID_MEMBER int(11) NOT NULL default '0',
  subject tinytext,
  posterName tinytext NOT NULL,
  posterEmail tinytext,
  posterTime bigint(20) default NULL,
  posterIP tinytext NOT NULL,
  smiliesEnabled tinyint(4) NOT NULL default '1',
  modifiedTime bigint(20) default NULL,
  modifiedName tinytext,
  body text,
  icon tinytext,
  attachmentSize int(11) NOT NULL default '0',
  attachmentFilename tinytext,
  PRIMARY KEY  (ID_MSG),
  KEY ID_TOPIC (ID_TOPIC)
) TYPE=MyISAM;

#
# Dumping data for table `messages`
#

INSERT INTO messages VALUES (1, 1, -1, 'Welcome to YaBB SE!', 'YaBB SE Dev Team', 'yabbsedev@yabb.info', 1007831725, '65.93.102.203', 1, NULL, NULL, 'Welcome to YaBB SE!\n\nWe hope you enjoy using your new forum.', 'xx','0','');
# --------------------------------------------------------

#
# Table structure for table `polls`
#

CREATE TABLE polls (
  ID_POLL int(11) NOT NULL auto_increment,
  question tinytext NOT NULL,
  votingLocked tinyint(4) NOT NULL default '0',
  votedMemberIDs text,
  option1 tinytext,
  option2 tinytext,
  option3 tinytext,
  option4 tinytext,
  option5 tinytext,
  option6 tinytext,
  option7 tinytext,
  option8 tinytext,
  votes1 int(11) NOT NULL default '0',
  votes2 int(11) NOT NULL default '0',
  votes3 int(11) NOT NULL default '0',
  votes4 int(11) NOT NULL default '0',
  votes5 int(11) NOT NULL default '0',
  votes6 int(11) NOT NULL default '0',
  votes7 int(11) NOT NULL default '0',
  votes8 int(11) NOT NULL default '0',
  PRIMARY KEY  (ID_POLL)
) TYPE=MyISAM;

#
# Table structure for table `reserved_names`
#

CREATE TABLE reserved_names (
  setting tinytext NOT NULL,
  value text NOT NULL
) TYPE=MyISAM;

#
# Dumping data for table `reserved_names`
#

INSERT INTO reserved_names VALUES ('matchword', '0');
INSERT INTO reserved_names VALUES ('matchcase', '1');
INSERT INTO reserved_names VALUES ('matchuser', '1');
INSERT INTO reserved_names VALUES ('matchname', '1');

# --------------------------------------------------------

#
# Table structure for table `settings`
#

CREATE TABLE settings (
  variable tinytext NOT NULL,
  value text NOT NULL
) TYPE=MyISAM;

#
# Dumping data for table `settings`
#

INSERT INTO settings VALUES ('news', 'YaBB SE! Just Installed!');
INSERT INTO settings VALUES ('agreement', 'You agree, through your use of this YaBB forum, that you will not post any material which is false, defamatory, inaccurate, abusive, vulgar, hateful, harassing, obscene, profane, sexually oriented, threatening, invasive of a person\'s privacy, or otherwise in violation of ANY law. This is not only humorous, but legal actions can be taken against you. You also agree not to post any copyrighted material unless the copyright is owned by you or you have consent from the owner of the copyrighted material. Spam, flooding, advertisements, chain letters, pyramid schemes, and solicitations are also inappropriate to this YaBB forum. \r\n\r\nNote that it is impossible for us to confirm the validity of posts on this YaBB forum. Please remember that we do not actively monitor the posted messages and are not responsible for their content. We do not warrant the accuracy, completeness or usefulness of any information presented. The messages express the views of the author, not necessarily the views of this YaBB forum. Anyone who feels that a posted message is objectionable is encouraged to notify an administrator of this forum immediately. We have the rights to remove objectionable content, within a reasonable time frame, if we determine that removal is necessary. This is a manual process, however, so please realize that we may not be able to remove or edit particular messages immediately. This policy goes for member profile information as well.\r\n\r\nYou remain solely responsible for the content of your messages, and you agree to indemnify and hold harmless this forum, and any related websites to this forum. We at this YaBB forum also reserve the right to reveal your identity (or any information we have about you) in the event of a complaint or legal action arising from any information posted by you.\r\n\r\nYou have the ability, as you register, to choose your username. We advise that you keep the name appropriate. With this user account you are about to register, you agree to never give your password out to another member, for your protection and for validity reasons. You also agree to NEVER use another member\'s account to post messages or browse this forum.\r\n\r\nAfter you register and log into this YaBB forum, you can fill out a detailed profile. It is your responsibility to present clean and accurate information. Any information we deem inaccurate or vulgar will be removed.\r\n\r\nPlease note that with each post, your IP address is recorded, in the event that you need to be banned from this YaBB forum or your ISP contacted. This will only happen in the event of a major violation of this agreement.\r\n\r\nAlso note that the software places a cookie, a text file containing bits of information (such as your username and password), in your browsers cache. This is ONLY used to keep you logged in/out. The software does not collect or sends any other form of information to your computer.');
INSERT INTO settings VALUES ('maxdays', '30');
INSERT INTO settings VALUES ('yabbinfo', '1007831605');
INSERT INTO settings VALUES ('enableStickyTopics', '0');
INSERT INTO settings VALUES ('todayMod', '0');
INSERT INTO settings VALUES ('karmaMode', '0');
INSERT INTO settings VALUES ('karmaTimeRestrictAdmins', '1');
INSERT INTO settings VALUES ('enablePreviousNext', '0');
INSERT INTO settings VALUES ('PreviousNext_back', '<sub>« previous</sub>');
INSERT INTO settings VALUES ('PreviousNext_forward', '<sub>next »</sub>');
INSERT INTO settings VALUES ('pollPostingRestrictions', '0');
INSERT INTO settings VALUES ('pollMode', '0');
INSERT INTO settings VALUES ('pollEditMode', '0');
INSERT INTO settings VALUES ('enableVBStyleLogin', '0');
INSERT INTO settings VALUES ('enableCompressedOutput', '0');
INSERT INTO settings VALUES ('karmaWaitTime', '6');
INSERT INTO settings VALUES ('karmaMinPosts', '0');
INSERT INTO settings VALUES ('karmaMemberGroups', '');
INSERT INTO settings VALUES ('karmaLabel', 'Karma:');
INSERT INTO settings VALUES ('karmaSmiteLabel', '[smite]');
INSERT INTO settings VALUES ('karmaApplaudLabel', '[applaud]');
INSERT INTO settings VALUES ('attachmentSizeLimit', '200');
INSERT INTO settings VALUES ('attachmentDirSizeLimit', '10240');
INSERT INTO settings VALUES ('attachmentUploadDir', '');
INSERT INTO settings VALUES ('attachmentUrl', '');
INSERT INTO settings VALUES ('attachmentExtensions', 'txt,jpg,gif,pdf');
INSERT INTO settings VALUES ('attachmentCheckExtensions', '1');
INSERT INTO settings VALUES ('attachmentShowImages', '0');
INSERT INTO settings VALUES ('attachmentEnable', '0');
INSERT INTO settings VALUES ('attachmentEnableGuest', '0');
INSERT INTO settings VALUES ('attachmentMemberGroups', 'Administrator');
INSERT INTO settings VALUES ('disableCaching', '0');
INSERT INTO settings VALUES ('enableInlineLinks', '0');
INSERT INTO settings VALUES ('enableSP1Info', '0');
# --------------------------------------------------------

#
# Table structure for table `topics`
#

CREATE TABLE topics (
  ID_TOPIC int(11) NOT NULL auto_increment,
  ID_BOARD tinyint(4) NOT NULL default '0',
  ID_MEMBER_STARTED int(11) NOT NULL default '0',
  ID_MEMBER_UPDATED int(11) NOT NULL default '0',
  ID_FIRST_MSG int(11) NOT NULL default '0',
  ID_LAST_MSG int(11) NOT NULL default '0',
  ID_POLL int(11) NOT NULL default '-1',
  numReplies int(11) NOT NULL default '0',
  numViews int(11) NOT NULL default '0',
  locked tinyint(4) NOT NULL default '0',
  notifies text,
  isSticky tinyint(4) NOT NULL default '0',
  PRIMARY KEY  (ID_TOPIC)
) TYPE=MyISAM;

#
# Dumping data for table `topics`
#

INSERT INTO topics VALUES (1, 1, -1, -1, 1, 1, -1, 0, 0, 0, NULL, 0);
