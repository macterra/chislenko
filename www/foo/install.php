<html>
<head>
	<title>YaBB SE Installer</title>
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
	<th bgcolor="#34699E"><font color="#FFFFFF">YaBB SE Installer</font></th>
</tr>
<tr>
	<td bgcolor="#f0f0f0">
<?php

if(!$step)
{
	$mode = ini_get("safe_mode");
	print "<br>Safe Mode Check:<br>";
	if ($mode)
	{
		print "<font color=red><br>Warning! It appears that safe mode is enabled.<br> <br>This version of the YaBB SE Installer will not work with safe mode enabled. You should download the safe mode installer from <a href=\"http://www.yabb.info/downloads.php\">yabb.info</a> and procede with your installation using that version.<br> <br>If you know for a fact that safe mode is NOT enabled, you may continue, although this is strongly discouraged.</font><br> <br>";
	}
	else
	{
	    print "<br>It appears that safe mode has been disabled.  You may continue. <br> <br>";
	}
	print "<a href=\"install.php?step=zero\">Click here</a> to continue.<br> <br>";
}
elseif($step == 'zero')
{
	$installdir = $HTTP_SERVER_VARS["DOCUMENT_ROOT"]."/yabbse";
	$installdir = str_replace("//","/",$installdir);

?>
This program will install YaBB SE on your webserver<br>
<form action="install.php?step=2" method="POST">
<table border=0 cellspacing=0 cellpadding=3>
<tr>
	<td valign=top><b>Install to directory:</b></td>
	<td><input type=text name="installdir" value="<?=$installdir?>" size="65"><br>
	<font size=1>Create this directory first, then chmod it to 777!</font></td>
</tr>
<tr>
	<td valign=top><b>Overwrite newer files:</b></td>
	<td><input type=checkbox name="overwrite" value="1"></td>
</tr>
</table>
<center>
<input type=submit value="Start installation &gt;">
</center>
</form>
	<?php
}
elseif($step == "2")
{
	if(!file_exists($installdir))
		die("Directory $installdir does not exist yet, please create it first");
	$directory = $installdir;
	if (!file_exists("$directory/Backups"))
	{
		mkdir("$directory/Backups", 0777);
		chmod("$directory/Backups", 0777);
	}
	if (!file_exists("$directory/attachments"))
	{
		mkdir("$directory/attachments", 0777);
		chmod("$directory/attachments", 0777);
	}

	echo "Installation starts now...<br><br>\n";
	$af = fopen("archive.ya","rb");
	while(!feof($af))
	{
		$data = explode('|^|', chop(fgets($af, 4086)));
		$filename = $directory."/".$data[1];
		if($data[0] == "dir" && !file_exists($filename) && $data[1] != ".")
		{
			mkdir($filename, 0777);
			chmod($filename, 0777);
		}
		if($data[0] == "file")
		{
			if(file_exists($filename) && filemtime($filename) >= $data[3] && !$overwrite)
				echo "Skipping $filename because it's a newer version or a version of the same age<br>\n";
			else
			{
				$buffer = fread($af, $data[2]);
				$fp = fopen($filename, "wb");
				fputs($fp, $buffer, $data[2]);
				fclose($fp);
				chmod($filename, 0666);
				touch($filename, $data[3]);
				//echo system("chown $filename $chownuser");
				echo "Wrote $filename ...<br>\n";
			}
		}
	}
	fclose($af);
	echo "<form action=\"install.php?step=3\" method=\"POST\">
		<input type=hidden name=\"installdir\" value=\"$installdir\">
		<input type=submit value=\"Proceed &gt;\"></form>";
}
elseif($step==3)
{
?>
You will now have to set some settings you need to set =)
<form action="install.php?step=4" method="POST">
<table border=0 cellspacing=0 cellpadding=3>
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
	<font size=1>Fill in the name of the database you want to use to let YaBB SE store it's data in, <b>make sure it is empty or at least doesn't contain important info, it's very likely that it will be destroyed<br>If this database does not exist, we will try to create it.</b></font></td>
</tr>
	<tr>
	<td valign=top><b>MySQL Database prefix:</b></td>
	<td><input type=text name="dbprefix" value="yabbse_"><br>
	<font size=1>Fill in the prefix you would like to use on all your table names.  You may leave this blank.  This value will be prepended to all table names to allow for multiple YaBB SE installations in a single database.</font></td>
</tr>
</table>
<center>
<input type=hidden name="installdir" value="<?=$installdir?>">
<input type=submit value="Proceed &gt;"> (Only proceed if successful!)
</center>
</form>
<?php
}
elseif($step==4)
{
	$goon = 1;
	$settingsArray = file("$installdir/Settings.php");
	for ($i = 0; $i< sizeof($settingsArray); $i++)
		$settingsArray[$i]=trim($settingsArray[$i]);

	// create a unique encryption key
	$key = md5(uniqid(microtime(),1));
	
	if (!isset($dbprefix)){ $dbprefix = ""; }
	$settingsArray[59] = "\$db_name = \"$dbname\";";
	$settingsArray[60] = "\$db_user = \"$dbuser\";";
	$settingsArray[61] = "\$db_passwd = \"$dbpassword\";";
	$settingsArray[62] = "\$db_server = \"$dbserver\";";
	$settingsArray[63] = "\$db_prefix = \"$dbprefix\";";
	$settingsArray[78] = "\$encryption_key = \"$key\";";

	$fp = fopen("$installdir/Settings.php", "w");
	foreach($settingsArray as $row)
		fputs($fp, "$row\n");
	fclose ($fp);

	$linkid = mysql_connect($dbserver, $dbuser, $dbpassword);
	if(!$linkid)
	{
		echo "Cannot connect to database server with given data";
		$goon = 0;
	}
	if ($goon)
	{
		mysql_query("CREATE DATABASE IF NOT EXISTS $dbname");
	}
	if($goon)
	{
		if(!mysql_select_db($dbname, $linkid))
		{
			echo "Cannot use database $dbname, connection to server succeeded by the way.";
			$goon = 0;
		}
	}
	if($goon) // It's time to dump ;)
	{
		$fd = fopen ("$installdir/yabb.sql", "r");

		$query = "";
		$counter = 0;
		while (!feof ($fd)) {
			$line = fgets($fd, 4096);
			$line = chop($line);

			// add the prefix
			$tablesFrom = array(" log_karma", " log_banned", " log_boards", " banned", " boards", " categories", " censor", " instant_messages", " log_clicks", " log_floodcontrol", " log_mark_read", " log_online", " log_topics", " membergroups", " members", " messages", " polls", " reserved_names", " settings", " topics");
			$tablesTo = array(" {$dbprefix}log_karma"," {$dbprefix}log_banned", " {$dbprefix}log_boards", " {$dbprefix}banned", " {$dbprefix}boards", " {$dbprefix}categories", " {$dbprefix}censor", " {$dbprefix}instant_messages", " {$dbprefix}log_clicks", " {$dbprefix}log_floodcontrol", " {$dbprefix}log_mark_read", " {$dbprefix}log_online", " {$dbprefix}log_topics", " {$dbprefix}membergroups", " {$dbprefix}members", " {$dbprefix}messages", " {$dbprefix}polls", " {$dbprefix}reserved_names", " {$dbprefix}settings", " {$dbprefix}topics");
			$line = str_replace($tablesFrom, $tablesTo, $line);

			if(!ereg("^#",$line)) $query .= $line;
			if((ereg(";\n?$",$line) or feof($fd)) and $query != "") {
				$counter++;
				echo "<i>Inserting data package $counter</i><br>";
				if (!mysql_query($query))
					print "<i>Errors: ".mysql_error()."</i><br><br>\n";
				else
					print "<i>No Errors</i><br><br>\n";
				$query = "";
			}
			if($count%200==0)
				set_time_limit(240);
		}
		fclose ($fd);
?>
		<center>
	<form action="install.php?step=5" method="POST">
	<input type=hidden name="dbuser" value="<?=$dbuser?>">
	<input type=hidden name="dbpassword" value="<?=$dbpassword?>">
	<input type=hidden name="dbprefix" value="<?=$dbprefix?>">
	<input type=hidden name="dbname" value="<?=$dbname?>">
	<input type=hidden name="dbserver" value="<?=$dbserver?>">
	<input type=hidden name="installdir" value="<?=$installdir?>">
	<input type="submit" value="Proceed &gt;">
	</form>
	</center>
	<?php
	}
}
elseif($step==5)
{
?>
<script>
	function updateFields() {
	  if (document.layers || document.all || document.getElementById) {
	    pForm = document.step6form;
		pForm.imagesdir.value = pForm.boardurl.value + "/YaBBImages";
		pForm.facesurl.value = pForm.boardurl.value + "/YaBBImages/avatars";
	  }
	}
</script>
We now need to ask you for the forum URLs so you can use YaBB SE
<form action="install.php?step=6" method="POST" name="step6form">
<table border=0 cellspacing=0 cellpadding=3>
<tr>
	<td valign=top><b>Board URL:</b></td>
	<td><input type=text name="boardurl" value="http://www.myserver.com/yabbse" size="65" onChange="updateFields();"><br>
	<font size=1>This is the url of your site without the trailing '/'.</font></td>
</tr>
<tr>
	<td valign=top><b>Images URL:</b></td>
	<td><input type=text name="imagesdir" value="http://www.myserver.com/yabbse/YaBBImages" size="65"><br>
	<font size=1>This is the location of your yabb images including the buttons, icons etc.</font></td>
</tr>
<tr>
	<td valign=top><b>Faces URL:</b></td>
	<td><input type=text name="facesurl" value="http://www.myserver.com/yabbse/YaBBImages/avatars" size="65"><br>
	<font size=1>This is the location of the avatars.  It is, by default, the avatars sub-directory in your images folder.</font></td>
</tr>
</table>
<center>
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
elseif($step==6)
{
$settingsArray = file("$installdir/Settings.php");
for ($i = 0; $i< sizeof($settingsArray); $i++)
	$settingsArray[$i]=trim($settingsArray[$i]);

// do the input ones
$settingsArray[42] = "\$boardurl = \"$boardurl\";				# URL of your board's folder (without trailing '/')";
$settingsArray[71] = "\$facesurl = \"$facesurl\";				# URL to your avatars folder";
$settingsArray[72] = "\$imagesdir = \"$imagesdir\";				# URL to your images directory";

$settingsArray[68] = "\$boarddir = \"$installdir\"; 				# The absolute path to the board's folder (usually can be left as '.')";

// now generate others
$settingsArray[69] = "\$sourcedir = \"$installdir/Sources\"; 				# Directory with YaBB source files";
$settingsArray[70] = "\$facesdir = \"$installdir/YaBBImages/avatars\";				# Absolute Path to your avatars folder";
$settingsArray[73] = "\$ubbcjspath = \"$boardurl/ubbc.js\";	                        # Web path to your 'ubbc.js' REQUIRED for post/modify to work properly!";
$settingsArray[74] = "\$faderpath = \"$boardurl/fader.js\";				# Web path to your 'fader.js'";
$settingsArray[75] = "\$helpfile = \"$boardurl/YaBBHelp/index.html\";				# Location of your help file;";

$fp = fopen("$installdir/Settings.php", "w");
foreach($settingsArray as $row)
	fputs($fp, "$row\n");
fclose ($fp);

?>
		<center>
	<form action="install.php?step=7" method="POST">
	<br><br>Settings.php was modified successfully<br>
	<input type=hidden name="dbuser" value="<?=$dbuser?>">
	<input type=hidden name="dbpassword" value="<?=$dbpassword?>">
	<input type=hidden name="dbprefix" value="<?=$dbprefix?>">
	<input type=hidden name="dbname" value="<?=$dbname?>">
	<input type=hidden name="dbserver" value="<?=$dbserver?>">
	<input type=hidden name="installdir" value="<?=$installdir?>">
	<input type="submit" value="Proceed &gt;">
	</form>
	</center>
	<?php
}
elseif ($step==7)
{
?>
You will now have to set up an adminstrator account
<form action="install.php?step=8" method="POST">
<table border=0 cellspacing=0 cellpadding=3>
<tr>
	<td valign=top><b>Your username:</b></td>
	<td><input type=text name="username" value=""><br>
	<font size=1>You can choose the username you'd like to have, this account will automatically get admin rights</font></td>
</tr>
<tr>
	<td valign=top><b>Password:</b></td>
	<td><input type=password name="password1"><br>
	<font size=1>Fill in your prefered password here, remember it well!</font></td>
</tr>
<tr>
	<td valign=top><b>Password again:</b></td>
	<td><input type=password name="password2"><br>
	<font size=1>Just for verification</font></td>
</tr>
<tr>
	<td valign=top><b>E-Mail:</b></td>
	<td><input type=text name="email" size=20></td>
</tr>
<tr>
	<td valign=top><b>Skip this:</b></td>
	<td><input type=checkbox name="skip"> <font size=1>Only check this if you DON'T want to set up an admin account.  Most likely this will only happen if you intend to continue and convert an existing Y1G board.</font></td>
</tr>
</table>
<center>
<input type=hidden name="installdir" value="<?=$installdir?>">
<input type=hidden name="dbuser" value="<?=$dbuser?>">
<input type=hidden name="dbpassword" value="<?=$dbpassword?>">
<input type=hidden name="dbprefix" value="<?=$dbprefix?>">
<input type=hidden name="dbname" value="<?=$dbname?>">
<input type=hidden name="dbserver" value="<?=$dbserver?>">
<input type=submit value="Proceed &gt;">
</center>
</form>
<?php
}
elseif($step == 8)
{
   include("$installdir/Settings.php");
	if ($skip != 'on')
	{
   if($password1 != $password2)
      die("Passwords did not match");
   $linkid = mysql_connect($db_server, $db_user, $db_passwd);
   mysql_select_db($db_name, $linkid);
   $queryPasswdPart = crypt($password1,substr($password1,0,2));
   // This will have to change obviously ;)
   $result=mysql_query("INSERT INTO {$db_prefix}members (memberName, realName, passwd, emailAddress, memberGroup, posts, personalText, avatar, dateRegistered, hideEmail) VALUES ('".addslashes($username)."', '".addslashes($username)."', '$queryPasswdPart', '$email', 'Administrator', '0', '', 'blank.gif', '".time()."', '0');");
   if (!$result){
   echo "Error: ".mysql_error();
   exit();}
print "An administrator account has been created, you can now proceed to your newly created board.<B>Please remember to delete install.php or rename it so it can't be executed, and to chmod your installation dir to something other than 777</B><br>";
	}

?>
	<form action="converter.php?step=1b" method="POST" name="theForm">
	<input type=hidden name="installdir" value="<?=$installdir?>">
	<input type=hidden name="dbuser" value="<?=$dbuser?>">
	<input type=hidden name="dbpassword" value="<?=$dbpassword?>">
	<input type=hidden name="dbprefix" value="<?=$dbprefix?>">
	<input type=hidden name="dbname" value="<?=$dbname?>">
	<input type=hidden name="dbserver" value="<?=$dbserver?>">
	Please follow one of the links below:<p><blockquote><a href="<?=$boardurl?>/index.php">Go to your board</a><br><a href="javascript:theForm.submit();">Convert an existing Y1G board data files</a><blockquote><p>Good luck!<br>The YaBB SE team.</form>
	<?php
}
?>
	</td>
</tr>
</table></center>
</body>
</html>
