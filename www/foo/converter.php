<html>
<head>
	<title>YaBB 1 Gold to YaBB SE Converter</title>
<style>
<!--
body {
	font-family : Verdana;
	font-size : 10pt;
}
td {
	font-size : 10pt;
}
-->
</style>
</head>
<body bgcolor="#FFFFFF">
<center><table border=0 cellspacing=1 cellpadding=4 bgcolor="#000000" width=90%>
<tr>
	<th bgcolor="#34699E"><font color="#FFFFFF">YaBB 1 Gold to YaBB SE Converter</font></th>
</tr>
<tr>
	<td bgcolor="#f0f0f0">
<?php

//Added to allow larger forums to convert without timing out - Jeff
set_time_limit(0);

if (!$step)
{
?>
<form action="converter.php?step=1a" method="POST">
This program will convert an existing Y1G board into an existing Yabb SE install.<p>
You must meet the following requirements to continue:
	<ul><li>You must have YaBB SE installed.  If it is not <a href="install.php">click here</a>.</li>
	<li>You must have YaBB SE installed with the same database prefix as you intend to convert to. i.e. if you are converting and intend to use the prefix yabbse2_ you must have, at some point, set up a YaBB SE install using that prefix.  Otherwise, this converter will be trying to insert data into tables that don't exist.</li>
	<li>You must have not have the encrypt passwords mod installed.  If you <b>must</b> notify your users they will have to use the 'forgot password' function to reset their passwords.  YaBB SE password encryption is incompatible with the encrypt password mod. Be prepared to change your passwords</li></ul>
<br><font size=1>Note: On Windows-based hosts, usernames are case-insensitive.  This may result in some posts being mislabeled as 'Guest' postings when in reality, a user simply posted while logged in with a different case (i.e. alexandra as opposed to Alexandra) for more details on this, and ways to get around the problem, please visit the <a href="http://www.yabb.info/community/">YaBB SE Community</a>.</font><br>
<center>
<input type=submit value="Proceed &gt;">
</center>
</form>
<?php
}
elseif($step == '1a')
{
	$mode = ini_get("safe_mode");
?>
<form action="converter.php?step=1b" method="POST">
We are now determining whether PHP is currently running in safe mode.<p>
<?php
	if ($mode)
	{
	?>
It appears that you are running in safe mode.  While the conversion process will work for the most part, the following steps will be disabled to prevent errors.
		<ul>
		<li><i>template.html conversion</i> - you will have to manually replace your YaBB SE template.html</li>
		<li><i>Settings.pl conversion</i> - you will have to update Settings.php manually</li>
		</ul>
The rest of the conversion should procede successfully though.<p>
	<?php
	}
	else
	{
	?>
It appears that safe mode is disabled.  All conversion steps should be successful.<p>
	<?php
	}
?>
<center>
<input type=hidden name="from" value="intro">
<input type=submit value="Proceed &gt;">
</center>
</form>
<?php

}
if($step=='1b')
{
	$y1gdir = $HTTP_SERVER_VARS["DOCUMENT_ROOT"]."/cgi-bin/YaBB";
	$y1gdir = str_replace("//","/",$y1gdir);
	$boardsdir = "$y1gdir/Boards";
	$membersdir = "$y1gdir/Members";
	$datadir = "$y1gdir/Messages";
	$vardir = "$y1gdir/Variables";
?>
This program will convert an existing YaBB 1 Gold install, into an existing YaBB SE install.  If you have not installed YaBB SE yet, please do so.<br>

<script>
	function updateFields() {
	  if (document.layers || document.all || document.getElementById) {
	    pForm = document.step1bform;
		pForm.boardsdir.value = pForm.y1gdir.value + "/Boards";
		pForm.membersdir.value = pForm.y1gdir.value + "/Members";
		pForm.datadir.value = pForm.y1gdir.value + "/Messages";
		pForm.vardir.value = pForm.y1gdir.value + "/Variables";
	  }
	}
</script>

<form action="converter.php?" method="POST" name="step1bform">
<table border=0 cellspacing=0 cellpadding=3>
<tr>
	<td valign=top><b>YaBB 1 Gold Install directory:</b></td>
	<td><input type=text name="y1gdir" value="<?=$y1gdir?>" size="65"onChange="updateFields();"><br>
	<font size=1>This directory is needed so we can copy your old settings.pl into the new settings.php</font></td>
</tr>
<tr>
	<td valign=top><b>YaBB 1 Gold boards directory:</b></td>
	<td><input type=text name="boardsdir" value="<?=$boardsdir?>" size="65"></td>
</tr>
<tr>
	<td valign=top><b>YaBB 1 Gold members directory:</b></td>
	<td><input type=text name="membersdir" value="<?=$membersdir?>" size="65"></td>
</tr>
<tr>
	<td valign=top><b>YaBB 1 Gold data directory:</b></td>
	<td><input type=text name="datadir" value="<?=$datadir?>" size="65"></td>
</tr>
<tr>
	<td valign=top><b>YaBB 1 Gold variables directory:</b></td>
	<td><input type=text name="vardir" value="<?=$vardir?>" size="65"></td>
</tr>
</table>
<?php
if ($from == 'install')
{
?>
<br><font size=1>Note: On Windows-based hosts, usernames are case-insensitive.  This may result in some posts being mislabeled as 'Guest' postings when in reality, a user simply posted while logged in with a different case (i.e. alexandra as opposed to Alexandra) for more details on this, and ways to get around the problem, please visit the <a href="http://www.yabb.info/community/">YaBB SE Community</a>.</font><br>
<input type=hidden name="dbuser" value="<?=$dbuser?>">
<input type=hidden name="dbpassword" value="<?=$dbpassword?>">
<input type=hidden name="dbprefix" value="<?=$dbprefix?>">
<input type=hidden name="dbname" value="<?=$dbname?>">
<input type=hidden name="dbserver" value="<?=$dbserver?>">
<input type=hidden name="installdir" value="<?=$installdir?>">
<input type=hidden name="dbpurge" value="on">
<input type=hidden name="step" value="3">
<?php
}
else
{
	print "<input type=hidden name=\"step\" value=\"2\">";
}
?>
<center>
<input type=submit value="Start Conversion &gt;">
</center>
</form>
	<?php
}
elseif($step == "2")
{
	$installdir = $HTTP_SERVER_VARS["DOCUMENT_ROOT"]."/yabbse";
	$installdir = str_replace("//","/",$installdir);
?>	
Now to collect some information from you regarding your YaBB SE Install<br>
<form action="converter.php?step=3" method="POST">
<table border=0 cellspacing=0 cellpadding=3>
<tr>
	<td valign=top><b>YaBB SE Install directory:</b></td>
	<td><input type=text name="installdir" value="<?=$installdir?>" size="40"><br>
	<font size=1>This directory is needed so we can copy your old settings.pl into the new settings.php</font></td>
</tr>
<tr>
	<td valign=top><b>MySQL server name:</b></td>
	<td><input type=text name="dbserver" value="localhost"><br>
	<font size=1>This is nearly always localhost, so if you don't know use localhost</font></td>
</tr>
<tr>
	<td valign=top><b>MySQL username:</b></td>
	<td><input type=text name="dbuser" value=""><br>
	<font size=1>Fill in the username you need to connect to your MySQL database, if you don't know, try the username of your ftp account, most of the times those two are equal.</font></td>
</tr>
<tr>
	<td valign=top><b>MySQL password:</b></td>
	<td><input type=password name="dbpassword" value=""><br>
	<font size=1>Fill in the password you need to connect to your MySQL database, if you don't know, try the password of your ftp account, most of the times they're the same.</font></td>
</tr>
	<tr>
	<td valign=top><b>MySQL Database name:</b></td>
	<td><input type=text name="dbname" value="yabbse"><br>
	<font size=1>Fill in the name of the database you want to use to let YaBB SE store it's data in, <b>make sure it is empty or at least doesn't contain important info, it's very likely that it will be destroyed</b></font></td>
</tr>
	<tr>
	<td valign=top><b>MySQL Database prefix:</b></td>
	<td><input type=text name="dbprefix" value="yabbse_"><br>
	<font size=1>Fill in the prefix you would like to use on all your table names.  You may leave this blank.  This value will be prepended to all table names to allow for multiple YaBB SE installations in a single database.</font></td>
</tr>
	<tr>
	<td valign=top><b>Purge the database?:</b></td>
	<td><input type=checkbox name="dbpurge"><br>
	<font size=1>Check this box if you want to purge the database of all conflicting data.  This will not empty the entire database - it will only empty those tables that may cause errors and/or duplication while using the converter.</font></td>
</tr>
</table>
<center>
<input type=hidden name="y1gdir" value="<?=$y1gdir?>">
<input type=hidden name="boardsdir" value="<?=$boardsdir?>">
<input type=hidden name="membersdir" value="<?=$membersdir?>">
<input type=hidden name="datadir" value="<?=$datadir?>">
<input type=hidden name="vardir" value="<?=$vardir?>">
<input type=submit value="Proceed &gt;"> (Only proceed if successful!)
</center>
</form>
<?php
}
elseif($step=="3")
{
?>	
Converting Settings...<br>
<form action="converter.php?step=4" method="POST">
<table border=0 cellspacing=0 cellpadding=3>
<tr><td>
<?php
	$dbcon = mysql_connect($dbserver, $dbuser, $dbpassword);
	mysql_select_db($dbname);

	// let's purge the database if it's selected
	if ($dbpurge == 'on')
	{
		print "Purging the database as per request...<blockquote>";
		$request = mysql_query("DELETE FROM {$dbprefix}instant_messages WHERE 1");
		$request = mysql_query("ALTER TABLE {$dbprefix}instant_messages AUTO_INCREMENT=1");
		print "{$dbprefix}instant_messages purged<br>";
		$request = mysql_query("DELETE FROM {$dbprefix}members WHERE 1");
		$request = mysql_query("ALTER TABLE {$dbprefix}members AUTO_INCREMENT=1");
		print "{$dbprefix}members purged<br>";
		$request = mysql_query("DELETE FROM {$dbprefix}categories WHERE 1");
		$request = mysql_query("ALTER TABLE {$dbprefix}categories AUTO_INCREMENT=1");
		print "{$dbprefix}categories purged<br>";
		$request = mysql_query("DELETE FROM {$dbprefix}boards WHERE 1");
		$request = mysql_query("ALTER TABLE {$dbprefix}boards AUTO_INCREMENT=1");
		print "{$dbprefix}boards purged<br>";
		$request = mysql_query("DELETE FROM {$dbprefix}topics WHERE 1");
		$request = mysql_query("ALTER TABLE {$dbprefix}topics AUTO_INCREMENT=1");
		print "{$dbprefix}topics purged<br>";
		$request = mysql_query("DELETE FROM {$dbprefix}messages WHERE 1");
		$request = mysql_query("ALTER TABLE {$dbprefix}messages AUTO_INCREMENT=1");
		print "{$dbprefix}messages purged<br>";
		$request = mysql_query("DELETE FROM {$dbprefix}membergroups WHERE grouptype=1");
		$request = mysql_query("ALTER TABLE {$dbprefix}membergroups AUTO_INCREMENT=8");
		print "{$dbprefix}membergroups purged<br>";
		print "</blockquote><p>";
	}
	// lets copy over the variables and the settings first 
	$agreementFile = file("$vardir/agreement.txt");
	$censorFile = file("$vardir/censor.txt");
	$membergroupsFile = file ("$vardir/membergroups.txt");
	$newsFile = file ("$vardir/news.txt");
	$oldestmesFile = file ("$vardir/oldestmes.txt");
	$reserveFile = file ("$vardir/reserve.txt");
	$reservecfgFile = file ("$vardir/reservecfg.txt");

	// now, lets create the array of variables to be injected into the settings table
	$settingsVars = array();
	$settingsVars['agreement'] = mysql_escape_string(implode("",$agreementFile));
	$settingsVars['news'] = mysql_escape_string(implode("",$newsFile));
	$settingsVars['maxdays'] = mysql_escape_string(trim($oldestmesFile[0]));
	foreach($settingsVars as $key=>$value)
	{
		$request = mysql_query("SELECT 1 FROM {$dbprefix}settings WHERE variable='$key' LIMIT 1");
		if (mysql_num_rows($request) == 0)
			$request = mysql_query("INSERT INTO {$dbprefix}settings (value,variable) VALUES ('$value','$key')");
		else
			$request = mysql_query("UPDATE {$dbprefix}settings SET value='$value' WHERE variable='$key' LIMIT 1");

		if (mysql_error() != '')
			print "<font color=red>Error editing table ({$dbprefix}settings).  Unable to set $key=".substr($value,0,50)."</font><br>";
		else
			print "Editing table ({$dbprefix}settings). Setting $key=".htmlspecialchars(substr($value,0,50))."..<br>";
	}
	unset($settingsVars,$agreementFile,$newsFile,$oldestmesFile);	// clear up some memory

	// now lets deal with the reserved words settings
	$reserved = array();
	$reserved['matchword'] = (trim($reservecfgFile[0])=='checked')?'1':'0';
	$reserved['matchcase'] = (trim($reservecfgFile[1])=='checked')?'1':'0';
	$reserved['matchuser'] = (trim($reservecfgFile[2])=='checked')?'1':'0';
	$reserved['matchname'] = (trim($reservecfgFile[3])=='checked')?'1':'0';
	foreach($reserved as $key=>$value)
	{
		$request = mysql_query("SELECT 1 FROM {$dbprefix}reserved_names WHERE setting='$key' LIMIT 1");
		if (mysql_num_rows($request) == 0)
			$request = mysql_query("INSERT INTO {$dbprefix}reserved_names (value,setting) VALUES ('$value','$key')");
		else
			$request = mysql_query("UPDATE {$dbprefix}reserved_names SET value='$value' WHERE setting='$key' LIMIT 1");
	
		$request = mysql_query("UPDATE {$dbprefix}reserved_names SET value='$value' WHERE setting='$key' LIMIT 1");
		if (mysql_error() != '')
			print "<font color=red>Error editing table ({$dbprefix}reserved_names).  Unable to set $key=$value</font><br>";
		else
			print "Editing table ({$dbprefix}reserved_names). Setting $key=$value..<br>";
	}
	unset ($reserved,$reservecfgFile);

	// now the reserved words
	foreach ($reserveFile as $word)
	{
		$word = mysql_escape_string(trim($word));
		$request = mysql_query("INSERT INTO {$dbprefix}reserved_names (setting,value) VALUES ('word','$word')");
		if (mysql_affected_rows() == 0)
			print "<font color=red>Error editing table ({$dbprefix}reserved_names).  Unable to insert word ($word)</font><br>";
		else
			print "Editing table ({$dbprefix}reserved_names). Inserting reserved word ($word)<br>";
	}
	unset ($reserveFile);

	// and the ever-entertaining censor
	foreach ($censorFile as $entry)
	{
		if (trim($entry) != '')
		{
			list($word,$replacement) = explode("=",$entry);
			$word = mysql_escape_string(trim($word));
			$replacement = mysql_escape_string(trim($replacement));
			$request = mysql_query("INSERT INTO {$dbprefix}censor (vulgar,proper) VALUES ('$word','$replacement')");
			if (mysql_affected_rows() < 0)
				print "<font color=red>Error editing table ({$dbprefix}censor).  Unable to insert $word=".substr($replacement,0,50)."</font><br>";
			else
				print "Editing table ({$dbprefix}censor). Inserting $word=".substr($replacement,0,50)."<br>";
		}
	}
	unset ($censorFile);

	// now the membergroups
	for ($i = 7; $i < sizeof($membergroupsFile); $i++)
	{
		if (trim($membergroupsFile[$i]) != '')
		{
			$membergroupsFile[$i] = mysql_escape_string(trim($membergroupsFile[$i]));
			$request = mysql_query("INSERT INTO {$dbprefix}membergroups (membergroup,grouptype) VALUES ('$membergroupsFile[$i]','1')");
			if (mysql_affected_rows() < 0)
				print "<font color=red>Error editing table ({$dbprefix}membergroups).  Unable to insert $membergroupsFile[$i]</font><br>";
			else
				print "Editing table ({$dbprefix}membergroups). Inserting $membergroupsFile[$i]<br>";
		}
	}
	unset ($membergroupsFile);
	?>
</td></tr></table>
<center>
<input type=hidden name="y1gdir" value="<?=$y1gdir?>">
<input type=hidden name="boardsdir" value="<?=$boardsdir?>">
<input type=hidden name="membersdir" value="<?=$membersdir?>">
<input type=hidden name="datadir" value="<?=$datadir?>">
<input type=hidden name="vardir" value="<?=$vardir?>">
<input type=hidden name="dbuser" value="<?=$dbuser?>">
<input type=hidden name="dbpassword" value="<?=$dbpassword?>">
<input type=hidden name="dbprefix" value="<?=$dbprefix?>">
<input type=hidden name="dbname" value="<?=$dbname?>">
<input type=hidden name="dbserver" value="<?=$dbserver?>">
<input type=hidden name="installdir" value="<?=$installdir?>">
<input type=submit value="Proceed &gt;"> (Only proceed if successful!)
</center>
</form>
<?php
}
elseif($step=="4")
{
?>
We are now going to attempt to convert your member data files.<br>
<form action="converter.php?step=5" method="POST">
<table border=0 cellspacing=0 cellpadding=3>
<tr><td>
<?php
	$dh = opendir($membersdir);
	print "Importing Member Data:<br><blockquote>";
	$users = array();

	$dbcon = mysql_connect($dbserver, $dbuser, $dbpassword);
	mysql_select_db($dbname);

	while (($file = readdir($dh)) !== false)
	{
		$extension = substr(strrchr($file, "."), 1);
		if ($extension != 'dat') { continue; }

		$user = substr($file,0,strrpos($file,"."));
		$userData = file ("$membersdir/$file");
		for ($i = 0; $i < sizeof($userData); $i++)
			$userData[$i] = mysql_escape_string(trim(htmlspecialchars($userData[$i])));
		$password = crypt (stripcslashes($userData[0]),substr(stripcslashes($userData[0]),0,2));
		if (strtotime(stripcslashes($userData[14])) == -1) {
			if (preg_match ("/(\d\d)\/(\d\d)\/(\d\d)(.*)(\d\d)\:(\d\d)\:(\d\d)/i", stripcslashes($userData[14]), $matches)) {
				$userData[14] = strtotime("$matches[5]:$matches[6]:$matches[7] $matches[1]/$matches[2]/$matches[3]");
			} else {
				$userData[14] = time();
			}
		} else {
			$userData[14] = strtotime(stripcslashes($userData[14]));			
		}	
		$birthdate = strtotime(stripcslashes($userData[16]));
		if ($birthdate > 0) {
			$tmp = getdate($birthdate);
			$userData[16] = "$tmp[year]-$tmp[mon]-$tmp[mday]";
		} else {
			if (preg_match ("/(\d\d)\/(\d\d)\/(\d\d)(.*)(\d\d)\:(\d\d)\:(\d\d)/i", stripcslashes($userData[16]), $matches)) {
				$birthdate = strtotime("$matches[5]:$matches[6]:$matches[7] $matches[1]/$matches[2]/$matches[3]");
				$tmp = getdate($birthdate);
				$userData[16] = "$tmp[year]-$tmp[mon]-$tmp[mday]";
			} else {
				$userData[16] = "";
			}
		}
		$userData[19] = $userData[19]=='checked'?'1':0;
		$userData[5] = str_replace("&&","\n",$userData[5]);
		$userData[5] = str_replace("&amp;&amp;","\n",$userData[5]);
		if ($userData[4] == 'Guest') {$userData[4] = $userData[1]; }
		$request = mysql_query("INSERT INTO {$dbprefix}members (memberName,passwd,realName,emailAddress,websiteTitle,websiteUrl,signature,posts,membergroup,ICQ,AIM,YIM,gender,personalText,avatar,dateRegistered,location,birthdate,timeOffset,hideEmail) VALUES ('$user','$password','$userData[1]','$userData[2]','$userData[3]','$userData[4]','$userData[5]','$userData[6]','$userData[7]','$userData[8]','$userData[9]','$userData[10]','$userData[11]','$userData[12]','$userData[13]','$userData[14]','$userData[15]','$userData[16]','$userData[18]','$userData[19]')");
		if (mysql_error()=='')
		{
			print "$user added, ";
			$users[$user] = array($user,mysql_insert_id());
		}
		else
			print "<font color=red>$user not added</font>, ";
	}
	print "</blockquote><p>Inserting Instant Messages<br><blockquote>";
	closedir($dh);
	foreach($users as $user)
	{
		if (!file_exists("$membersdir/$user[0].msg")){ continue; }
		$data1 = file("$membersdir/$user[0].msg");
		foreach ($data1 as $theData)
		{
			if (trim($theData) == '') { continue; }
			$data = explode("|",trim($theData));

			$fromID = isset($users[$data[0]])?$users[$data[0]][1]:'-1';
			$fromName = isset($users[$data[0]])?$users[$data[0]][0]:'Guest';

			$data[1] = mysql_escape_string($data[1]);

			if (strtotime($data[2]) == -1) {
				if (preg_match ("/(\d\d)\/(\d\d)\/(\d\d)(.*)(\d\d)\:(\d\d)\:(\d\d)/i", $data[2], $matches)) {
					$data[2] = strtotime("$matches[5]:$matches[6]:$matches[7] $matches[1]/$matches[2]/$matches[3]");
				} else {
					$data[2] = time();
				}
			} else {
				$data[2] = strtotime($data[2]);
			}	
			
			$data[3] = mysql_escape_string(	$data[3]);

			if ($fromID != '-1')
				$request = mysql_query("INSERT INTO {$dbprefix}instant_messages (ID_MEMBER_FROM,ID_MEMBER_TO,fromName,toName,msgtime,subject,body,deletedBy) VALUES ('$fromID','$user[1]','$fromName','$user[0]','$data[2]','$data[1]','$data[3]','0')");

		}
		print ("$user[0]'s im's, ");
		if (file_exists("$membersdir/$user[0].imconfig"))
		{
			$imFile = file ("$membersdir/$user[0].imconfig");
			$imFile[0] = str_replace("|",",",trim($imFile));
			$imFile[1] = (trim($imFile[1])=='')?'0':'1';
			$request = mysql_query("UPDATE {$dbprefix}members SET im_email_notify='$imFile[1]',im_ignore_list='$imFile[0]' WHERE ID_MEMBER='$user[1]' LIMIT 1");
		}
	}
	foreach($users as $user)
	{
		if (!file_exists("$membersdir/$user[0].karma")){ continue; }
		$data1 = file("$membersdir/$user[0].karma");
		$field = (trim($data1[0])<0)?'karmaBad':'karmaGood';
		$val = abs(trim($data1[0]));
		$request = mysql_query("UPDATE {$dbprefix}members SET $field='$val' WHERE ID_MEMBER='$user[1]' LIMIT 1");
		print ("$user[0]'s karma, ");
	}
	print "</blockquote><br>";
?>
</td></tr></table>
<center>
<input type=hidden name="y1gdir" value="<?=$y1gdir?>">
<input type=hidden name="boardsdir" value="<?=$boardsdir?>">
<input type=hidden name="membersdir" value="<?=$membersdir?>">
<input type=hidden name="datadir" value="<?=$datadir?>">
<input type=hidden name="vardir" value="<?=$vardir?>">
<input type=hidden name="dbuser" value="<?=$dbuser?>">
<input type=hidden name="dbpassword" value="<?=$dbpassword?>">
<input type=hidden name="dbprefix" value="<?=$dbprefix?>">
<input type=hidden name="dbname" value="<?=$dbname?>">
<input type=hidden name="dbserver" value="<?=$dbserver?>">
<input type=hidden name="installdir" value="<?=$installdir?>">
<input type=submit value="Proceed &gt;"> (Only proceed if successful!)
</center>
</form>
<?php
}
elseif($step=="5")
{
?>
We are now going to attempt to convert your message files.  This may take a while.<br>
<form action="converter.php?step=6" method="POST">
<table border=0 cellspacing=0 cellpadding=3>
<tr><td>
<?php
$dbcon = mysql_connect($dbserver, $dbuser, $dbpassword);
mysql_select_db($dbname);

// first we need to load an array of all the members
$dh = opendir($membersdir);
print "Initializing member data for processing....";
$users = array();
while (($file = readdir($dh)) !== false)
{
	$extension = substr(strrchr($file, "."), 1);
	if ($extension != 'dat') { continue; }
	$user = substr($file,0,strrpos($file,"."));

	$request = mysql_query("SELECT ID_MEMBER FROM {$dbprefix}members WHERE memberName='$user' LIMIT 1");
	$row = mysql_fetch_row($request);
	$users[$user] = array($user,$row[0]);
}
closedir($dh);

// now load an array of the sticky topics
$stickyTopics = array();
if (file_exists("$boardsdir/$sticky.stk"))
{
	foreach(file("$boardsdir/$sticky.stk") as $line)
	{
		if (trim($line)!= '')
			$stickyTopics[] = trim($line);
	}
}

// now to create the categories
$dh = opendir($boardsdir);
$categories = array();
$boards = array();
while (($file = readdir($dh)) !== false)
{
	list ($cat,$extension) = explode ('.',$file);
	if ($extension != 'cat') { continue; }
	// we are now only looping for the categories

	$catFile = file ("$boardsdir/$file");
	$catname = mysql_escape_string(trim($catFile[0]));
	$catmembergroups = mysql_escape_string(trim($catFile[1]));
	$request = mysql_query("INSERT INTO {$dbprefix}categories (name,memberGroups) VALUES ('$catname','$catmembergroups')");
	$catid = mysql_insert_id();
	$categories[$cat] = $catid;

	print "Inserted category: $cat<br>";
	// now we are going to loop though for every board
	for ($i = 2; $i < sizeof($catFile); $i++)
	{
		$curboard = trim($catFile[$i]);
		if ($curboard==''){ continue; }

		// now to get some board information to insert into the db
		$boardFile = file ("$boardsdir/$curboard.dat");
		$boardname = mysql_escape_string(trim($boardFile[0]));
		$boarddesc = mysql_escape_string(trim($boardFile[1]));
		$boardmods = mysql_escape_string(trim(str_replace("|",",",$boardFile[2])));
		$request = mysql_query("INSERT INTO {$dbprefix}boards (ID_CAT,name,description,moderators) VALUES ('$catid','$boardname','$boarddesc','$boardmods')");
		$boardid = mysql_insert_id();

		print "&nbsp;&nbsp;Inserted board: $boardname<br>";
		// initialize the topics and posts count for later
		$numTopics = 0;
		$numPosts = 0;

		// now loop through for all the topics
		$boardFile = file ("$boardsdir/$curboard.txt");
		foreach ($boardFile as $boardFileEntry)
		{
			if (trim($boardFileEntry=='')){ continue; }
			$topicinfo = explode("|",trim($boardFileEntry));
			$topicid = $topicinfo[0];
			$locked = $topicinfo[8];
			$topicinfo = file("$datadir/$topicid.data");
			$numViews = trim($topicinfo[0]);
			$request = mysql_query("INSERT INTO {$dbprefix}topics (ID_BOARD,numViews,locked) VALUES ('$boardid','$numViews','$locked')");
			$id_topic = mysql_insert_id();

			$first = true;
			$numReplies = 0;
			$posterID = '';
			$msgid = '';
			// now to loop through for the messages			
			$messageFile = file ("$datadir/$topicid.txt");
			foreach($messageFile as $messageFileEntry)
			{
				if (trim($messageFileEntry)==''){ continue; }
				$messageinfo = explode("|",$messageFileEntry);
				for ($j = 0; $j < sizeof ($messageinfo); $j++)
					$messageinfo[$j] = mysql_escape_string(trim($messageinfo[$j]));
				$posterID = isset($users[$messageinfo[4]])?$users[$messageinfo[4]][1]:'-1';

				// $messageinfo[3] = strtotime($messageinfo[3]);

				if (strtotime($messageinfo[3]) == -1) {
					if (preg_match ("/(\d\d)\/(\d\d)\/(\d\d)(.*)(\d\d)\:(\d\d)\:(\d\d)/i", $messageinfo[3], $matches)) {
						$messageinfo[3] = strtotime("$matches[5]:$matches[6]:$matches[7] $matches[1]/$matches[2]/$matches[3]");
					} else {
						$messageinfo[3] = time();
					}
				} else {
					$messageinfo[3] = strtotime($messageinfo[3]);			
				}	

				if (strtotime($messageinfo[11]) == -1) {
					if (preg_match ("/(\d\d)\/(\d\d)\/(\d\d)(.*)(\d\d)\:(\d\d)\:(\d\d)/i", $messageinfo[11], $matches)) {
						$messageinfo[11] = strtotime("$matches[5]:$matches[6]:$matches[7] $matches[1]/$matches[2]/$matches[3]");
					} else {
						$messageinfo[11] = '';
					}
				} else {
					$messageinfo[11] = strtotime($messageinfo[11]);			
				}	

				$messageinfo[9] = !$messageinfo[9];
				$messageinfo[4] = ($messageinfo[4]=='Guest')?$messageinfo[1]:$messageinfo[4];
				$messageinfo[8] = preg_replace('/\[quote (.+?)]/i',"[quote]",$messageinfo[8]);

				$request = mysql_query("INSERT INTO {$dbprefix}messages (ID_MEMBER,posterName,posterIP,posterEmail,posterTime,subject,body,smiliesEnabled,modifiedName,modifiedTime,icon,ID_TOPIC) VALUES ('$posterID','$messageinfo[4]','$messageinfo[7]','$messageinfo[2]','$messageinfo[3]','$messageinfo[0]','$messageinfo[8]','$messageinfo[9]','$messageinfo[11]','$messageinfo[10]','$messageinfo[5]','$id_topic')");
				$msgid = mysql_insert_id();

				if ($first)
				{
					$first = false;
					$request = mysql_query("UPDATE {$dbprefix}topics SET ID_FIRST_MSG='$msgid',ID_MEMBER_STARTED='$posterID' WHERE ID_TOPIC='$id_topic' LIMIT 1");
				}
				else
					$numReplies ++;
			}
			// now, just for Andrea, we're going to try to import the notifies
			$notifies = array();
			if (file_exists("$datadir/$topicid.mail"))
			{
				$notifyList = file ("$datadir/$topicid.mail");
				foreach ($notifyList as $not)
				{
					$not = trim($not);
					$request = mysql_query("SELECT ID_MEMBER FROM {$dbprefix}members WHERE emailAddress='$not' LIMIT 1");
					if (mysql_num_rows($request) > 0)
					{
						$row = mysql_fetch_row($request);
						$notifies[] = $row[0];
					}
				}
			}
			$notify = (sizeof($notifies) > 0) ? implode(",",$notifies) : '';

			// and now we'll try to do sticky topics
			$isSticky = (sizeof($stickyTopics) > 0 && in_array($topicid,$stickyTopics))?1:0;

			$request = mysql_query("UPDATE {$dbprefix}topics SET ID_LAST_MSG='$msgid',ID_MEMBER_UPDATED='$posterID',numReplies='$numReplies',notifies='$notify',isSticky='$isSticky' WHERE ID_TOPIC='$id_topic' LIMIT 1");
			$request = mysql_query("UPDATE {$dbprefix}boards SET numPosts=numPosts+$numReplies+1,numTopics=numTopics+1 WHERE ID_BOARD='$boardid' LIMIT 1");
		}
	}
}

closedir($dh);
?>
</td></tr></table>
<center>
<input type=hidden name="y1gdir" value="<?=$y1gdir?>">
<input type=hidden name="boardsdir" value="<?=$boardsdir?>">
<input type=hidden name="membersdir" value="<?=$membersdir?>">
<input type=hidden name="datadir" value="<?=$datadir?>">
<input type=hidden name="vardir" value="<?=$vardir?>">
<input type=hidden name="dbuser" value="<?=$dbuser?>">
<input type=hidden name="dbpassword" value="<?=$dbpassword?>">
<input type=hidden name="dbprefix" value="<?=$dbprefix?>">
<input type=hidden name="dbname" value="<?=$dbname?>">
<input type=hidden name="dbserver" value="<?=$dbserver?>">
<input type=hidden name="installdir" value="<?=$installdir?>">
<input type=submit value="Proceed &gt;"> (Only proceed if successful!)
</center>
</form>
<?php
}
elseif($step=="6")
{
		$mode = ini_get("safe_mode");
?>
Conversion Complete!<br>
<form action="converter.php?step=7" method="POST">
<table border=0 cellspacing=0 cellpadding=3>
<tr><td>
<input type=checkbox name="copyt"> Yes, copy my template.html<br>
<input type=checkbox name="copys"> Yes, copy my settings<br>
</td></tr></table>
Your avatars and template images will not be copied over in this process.  You are responsible for copying over the necessary images from the YaBBImages dir and the avatars dir.<br>
<?php
	if ($mode)
	print "Note, you are running in safe mode, these functions will not be executed regardless of your selection.  Please press continue.<p>";
?>
<center>
<input type=hidden name="y1gdir" value="<?=$y1gdir?>">
<input type=hidden name="boardsdir" value="<?=$boardsdir?>">
<input type=hidden name="membersdir" value="<?=$membersdir?>">
<input type=hidden name="datadir" value="<?=$datadir?>">
<input type=hidden name="vardir" value="<?=$vardir?>">
<input type=hidden name="dbuser" value="<?=$dbuser?>">
<input type=hidden name="dbpassword" value="<?=$dbpassword?>">
<input type=hidden name="dbprefix" value="<?=$dbprefix?>">
<input type=hidden name="dbname" value="<?=$dbname?>">
<input type=hidden name="dbserver" value="<?=$dbserver?>">
<input type=hidden name="installdir" value="<?=$installdir?>">
<input type=submit value="Proceed &gt;">
</center>
</form>
<?php
}
elseif($step=="7")
{
?>
We are now copying your selections<br>
<table border=0 cellspacing=0 cellpadding=3>
<tr><td>
<?php
$mode = ini_get("safe_mode");
if (isset($copyt) && !$mode)
{
	if (copy("$y1gdir/template.html","$installdir/template.html"))
		print "template.html copied successfully.<br>";
	else
		print "<font color=red>template.html copy failed.</font><br>";
}
if (isset($copys) && !$mode)
{
	$settingsToCopy = array('guestaccess', 'mbname', 'Cookie_Length', 'RegAgree', 'emailpassword', 'emailnewpass', 'emailwelcome', "color{'titlebg'}", "color{'titletext'}", "color{'windowbg'}", "color{'windowbg2'}", "color{'windowbg3'}", "color{'catbg'}", "color{'bordercolor'}", "color{'fadertext'}", "color{'fadertext2'}", 'MenuType', 'curposlinks', 'profilebutton','allow_hide_email', 'showlatestmember', 'shownewsfader', 'Show_RecentBar', 'Show_MemberBar', 'showmarkread', 'showmodify', 'ShowBDescrip', 'showuserpic', 'showusertext', 'showgenderimage', 'showyabbcbutt', 'enable_ubbc', 'enable_news', 'allowpics', 'enable_guestposting', 'enable_notification', 'TopAmmount', 'MembersPerPage', 'maxdisplay', 'maxmessagedisplay', 'MaxMessLen', 'MaxSigLen', 'ClickLogTime', 'timeout', 'JrPostNum', 'FullPostNum', 'SrPostNum', 'GodPostNum', 'userpic_width', 'userpic_height');

	print"<p>";

	$copiedSettings = array();
	$settingsArray = file("$y1gdir/Settings.pl");
	for($i = 0; $i < sizeof($settingsArray); $i++)
	{
		foreach ($settingsToCopy as $curset)
		{
			if (substr($settingsArray[$i],1,strlen($curset))==$curset)
			{
				preg_match("/=(.*);/",$settingsArray[$i],$matches);
				$copiedSettings[$curset] = $matches[1];
				print "Copied value of $curset...<br>";
				continue;
			}
		}
	}

	print "<p>";

	$settingsArray = file("$installdir/Settings.php");
	$url = "";
	for($i = 0; $i < sizeof($settingsArray); $i++)
	{
		foreach ($copiedSettings as $set => $val)
		{
			$set = str_replace("{'","['",$set);
			$set = str_replace("'}","']",$set);
			if (substr($settingsArray[$i],1,strlen($set))==$set)
			{
				$settingsArray[$i] = preg_replace("/=(.*);/","= $val;",$settingsArray[$i],1);
				print "Replaced value of $set..with $val	<br>";
				continue;
			}
		}
	}

	$fp = fopen("$installdir/Settings.php", "w");
	foreach($settingsArray as $row)
		fputs($fp, $row);
	fclose ($fp);
}
include("$installdir/Settings.php");
?>
</td></tr></table>
<center><p>
Installation Complete!<p>
<a href="<?=$boardurl?>/index.php">Click Here</a> to go to your board<br><br>
</center>
<?php
}

?>
	</td>
</tr>
</table></center>
</body>
</html>
