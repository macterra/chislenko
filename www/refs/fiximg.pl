#/usr/bin/perl
#
# fiximg.pl version 1.3
#
# Written by Patrick Atoon <patricka@cs.kun.nl>.
#
# The kinky part is that it parses the images to find out the true
# height and width. Works only for JPG and GIF, though. Other images
# should be left alone.
#
# Modification history
#
# January 6, 1996 added two backslashes; Perl5 refuses to read a @ without
# a backslash in front... (Mark Koenen <markko@sci.kun.nl>).
#
# August 28, 1995 modified the regular expression matching that
# looks for the SRC = string to handle spaces around the = sign
# (Robert Wallace <wallace@ssd.intel.com>)
#
# No fear; a backup will be made under the name <html file>.bak,
# just in case this messes up things badly.
#
# Use  "fiximg.pl -h" to get the full manual
#


# Where to find the web-documents, same as httpd's DocumentRoot.
# CHANGE THIS TO YOUR DOCUMENTROOT!

$DocumentRoot = "home/sasha/www";



# Look for special flags
&parse_argv;

foreach $infile (@filenames)
{
    print "Fixing \"$infile\"\n";
    $outfile = $infile . ".tmp";

    # Determine path to $infile
    $i = rindex($infile, "/");

    if ($i >= 0)
    {
	$fileroot = substr($infile, 0, $i+1);
    }
    else
    {
	$fileroot = "./";
    }

    # Skip links
    if ($skiplinks && -l $infile)
    {
        print "Skipping linked file \"$infile\".\n";
        next;
    }

    if (!open(IN, $infile))
    {
	print "Cannot open file \"$infile\"\n";
        next;
    }

    if (!open(OUT, ">$outfile"))
    {
	print "Cannot open file \"$outfile\"\n";
        next;
    }

    while(<IN>)
    {
	chop;
	$line = $_;

	if ($line =~ /<IMG/i)
	{
	    $newline = "";

	    # Change all IMG tags on this line
	    while ($line =~ /<IMG/i)
	    {
                # Change to capitals; they are easier to locate.
		$line =~ s/<IMG/<IMG/gi;
		$begin = index($line, "<IMG");

		# Find the whole <IMG ...> tag.
		while (rindex($line, ">") < $begin)
		{
		    $inline = <IN>;
		    chop($inline);
		    $line .= " " . $inline;        # Add a safety space
		}

                # Cut out the <IMG ...> tag
		$end = index($line, ">", $begin);

		# Preserve the bit in front if this IMG tag.
		if ($begin != 0)
		{
		    $newline .= substr($line, 0, $begin);
		}

		$img_tag = substr($line, $begin, ($end-$begin)+1);

		if ($end < length($line))
		{
		    $line = substr($line, $end+1);
		}

		# Make sure the end of the IMG tag is also on this line
		$filename = $img_tag;
		$filename =~ s/.* SRC[\s]*=[\s]*([^\s>]*).*/$1/i;

		$filename =~ s/"//g;

		if ($filename =~ m|^/~|)
		{
		    $filename = &fixhomedir($filename);
		}
		elsif ($filename =~ m|^/|)
		{
		    $filename = $DocumentRoot . $filename;
		}
		else
		{
		    $filename = $fileroot . $filename;
		}

		# Now add WIDTH and HEIGHT to the IMG tag
		if ($filename =~ /.jpg$/i || $filename =~ /.jpeg$/i)
		{
		    $wh = &JPEG_size($filename);      # Determine the size
		    $img_tag =~ s/ *WIDTH=[0-9]*//i;  # Throw away old width
		    $img_tag =~ s/ *HEIGHT=[0-9]*//i; # Throw away old height
		    $img_tag =~ s/>/ $wh>/;           # Insert before ">"
		}
		elsif ($filename =~ /.gif$/i)
		{
		    $wh = &GIF_size($filename);       # Determine the size
		    $img_tag =~ s/ *WIDTH=[0-9]*//i;  # Throw away old width
		    $img_tag =~ s/ *HEIGHT=[0-9]*//i; # Throw away old height
		    $img_tag =~ s/>/ $wh>/;           # Insert before ">"
		}

		$newline .= $img_tag;
	    }

	    $line = $newline . "$line";
	}

	$line .= "\n";
	print OUT $line;
    }

    close(IN);
    close(OUT);

    # Safety first. ;-)
    system("/bin/mv $infile $infile.bak");
    system("/bin/mv $outfile $infile");
}

exit;

#########################################################################3
#
# Subroutines
#

# This is a little hack to determine the width and height in pixels of
# a JPEG image. It is a hack because it assumes that some markers
# exist which in fact don't. This should really cause no problems,
# however.
# Original written by Marcus E. Hennecke <marcush@leland.stanford.edu>

sub JPEG_size
{
    # Define marker types
    local($M_SOF0) = 0xC0;
    local($M_SOF15) = 0xCF;
    local($M_SOI) = 0xD8;
    local($M_EOI) = 0xD9;
    local($M_SOS) = 0xDA;
    local($M_COM) = 0xFE;
    local($l,$d,$h,$w);

    local($fn) = @_;

    if (!open(IMAGE, $fn))
    {
        print "Could not open file \"$fn\"!\n";
        return "";
    }

    # Check the first few bytes to see if this is a JPEG file. From the docs:
    #
    # o you can identify a JFIF file by looking for the following sequence:
    #   X'FF', SOI, X'FF', APP0, <2 bytes to be skipped>, "JFIF", X'00'.

    local($c1) = &read_1_byte;
    local($c2) = &read_1_byte;

    if ($c1 != 0xFF || $c2 != $M_SOI)
    {
        print("\"$fn\" is not a JPEG file!\n");
        close(IMAGE);
        return "";
    }

    # Go through the markers in the header. Stop when height and width are
    # determined or when end of header is reached

    while (1)
    {
        # Get the next marker
        local($db) = 0;
        $c1 = &read_1_byte;

        while ($c1 != 0xFF) 
        {
            $db++;
            $c1 = &read_1_byte;
        }

        while ($c1 == 0xFF)
        {
            $c1 = &read_1_byte;
        }

        if ($db)
        {
             print("Warning: garbage data found in JPEG file \"$fn\"\n");
        }

        # What type marker are we looking at?
        # Note that this first if statement is actually not quite correct.
        # It assumes that the markers SOF0 to SOF15 all exist and are in
        # order. In reality, they are in order, but SOF4, SOF8, and SOF12
        # do not exist. Nevertheless, these markers should not normally
        # appear in a JPEG file and so this if statement works.

        if ($c1 >= $M_SOF0 && $c1 <= $M_SOF15)
        {
            # Do we have width and height?
            ($l,$d,$h,$w) = unpack("nCnn", &read_n_bytes(7));

            $l = &ushort($l);
            $h = &ushort($h);
            $w = &ushort($w);
            close(IMAGE);
            return "WIDTH=${w} HEIGHT=${h}";
        }
        elsif ($c1 == $M_SOS || $c1 == $M_EOI)
        {
            # Did we reach header end?
            close(IMAGE);
            return "";
        }
        else
        {
            # Otherwise, skip this variable
            $l = &ushort(unpack("n", &read_n_bytes(2))) - 2;

            if ($l < 0)
            { 
                print("Erroneous JPEG marker length in file \"$fn\"!\n");
                close(IMAGE);
                return "";
            }

            &read_n_bytes($l);
        }
    }
}

#
# Determine the size of a GIF file
#
sub GIF_size
{
    local($fn) = @_;

    if (!open(IMAGE, $fn))
    {
        print "Could not open file \"$fn\"!\n";
        return "";
    }

    $read = &read_n_bytes(6);

    if ($read ne "GIF87a" && $read ne "GIF89a")
    {
        print "\"$fn\" is not a GIF file!\n";
        close(IMAGE);
        return "";
    }

    # Examine the Logical Screen Descriptor
    local($lsw, $lsh, $pf, $bg, $par) = unpack("vvCCC", &read_n_bytes(7));

    # Is it followed by a Global Color table?
    if ($pf & 0x80)
    {
        # Skip the Global Color Table
        local($GCTsize) = $pf & 0x07;
        &read_n_bytes(3 * (2 << $GCTsize));
    }

    # Go through the markers in the header. Stop when height and width are
    # determined or when end of header is reached

    while (1)
    {
        # Get the next marker
        $c = &read_1_byte;

        if ($c == 0x21)
        {
            #
            # This is an Extension.
            #

            # Read the label.
            $c = &read_1_byte;

            # Read the remainder of this Extension Block and while we're at it,
            # read all possible Data Sub-blocks as well.
            while ($blksize = &read_1_byte)
            {
                &read_n_bytes($blksize);
            }
        }
        elsif ($c == 0x2c)
        {
            #
            # This is the most holy of all... The Image Descriptor.
            #
            local($lp,$tp,$w,$h,$pf) = unpack("vvvvC", &read_n_bytes(9));
            $w = &ushort($w);
            $h = &ushort($h);
            close(IMAGE);
            return "WIDTH=${w} HEIGHT=${h}";
        }
        else
        {
            close(IMAGE);
            return "";
        }
    }
}

# Reads one byte. If EOF is reached, terminates with an error message.
sub read_1_byte
{
    return ord(getc(IMAGE));
}

# Reads N bytes. If EOF is reached, terminates with an error message.
sub read_n_bytes
{
    local($n) = @_;
    local($ch);
    read(IMAGE, $ch, $n) == $n || print("Premature EOF in GIF file \"$fn\"!\n");
    return $ch;
}

# Make a signed short unsigned.
sub ushort
{
    local($n) = @_;

    if ($n < 0)
    {
        $n += 65536;
    }

    return $n;
}


sub fixhomedir
{
    local($url) = @_;
    local($name) = $url;

    $name =~ s|^/~||;
    $name =~ s|/.*||g;

    if (length($name) < 1 || $name eq $url)
    {
	return $url;
    }

    open(pass, "/etc/passwd");

    while (<pass>)
    {
	($login, $passwd, $uid, $gid, $fullname, $homedir) = split(/:/);
	last if ($name eq $login);
    }

    close(pass);

    $url =~ s|^/~$name|$homedir/public_html/|;
    return $url;
}

sub parse_argv
{
    # Some defaults
    @filenames = ();
    $skiplinks = 1;

    while (@ARGV)
    {
        $arg = shift(@ARGV);
    
        if ($arg eq "-h")
        {
            &print_help;
            exit;
        }
        elsif ($arg eq "-l")
        {
            $skiplinks = 0;
        }
        else
        {
            push(@filenames, $arg);
        }
    }
}


sub print_help
{
    print <<EOF;

NAME
       fiximg.pl - add WIDTH and HEIGHT to IMG tags in HTML files.

SYNOPSIS
       fiximg.pl -h
       fiximg.pl [ options ] htmlfile [ ... ]

DESCRIPTION
       fiximg.pl examines HTML files and changes IMG tags to hold the WIDTH
       and HEIGHT sizes. Putting the size in IMG tags will speed up loading
       the page with Netscape. When HTML files are edited manually, putting
       in  the  sizes  is quite a hassle. fiximg.pl will try to locate each
       indicated image, and determine its size if it is a GIF or JPG image.

       Actually,  any  text  file  can  be fed to fiximg.pl and it will try
       to  change any IMG tags found. This can be useful if you want to
       use it on scripts or such that spew out HTML code.

       Images are located in three ways:

       1.     the filename of the image starts with a "/". In this case the
              DocumentRoot as set in fiximg.pl (change it to local path) is
              used.

       2.     the filename of the image starts with a "~". The user will be
              looked up in /etc/passwd, and his directory public_html will
              be used.

       3.     in  all  other  cases  the  path  to  the  html file is used.

       Unless  there  are aliases for certain image directories this scheme
       should work.

       The size is then put in the IMG tag, leaving intact everything other
       than  the old WIDTH and HEIGHT. This means that if the old WIDTH and
       HEIGHT  were used to resize the image, the effect will be discarded.

       The  original  htmlfile  will be backupped to its original name with
       ".bak" appended.

EXAMPLE USAGE
       To change a whole tree, try something like this:

              find web-docs -name "*.html" -print | xargs fiximg.pl

OPTIONS
       -h     Displays this manual page.

       -l     Treat symbolic links as well. The default is not to treat
              symbolic links, because the original file should be treated
              instead of the link to it.

SEE ALSO
       http://www.sci.kun.nl/thalia/guide/#fiximg
              For the latest version.

BUGS
       Some  JPEG  images  are known to break the algorithm that searches
       for  the  height  and  width.  The sizes will be set to rediculous
       values, distorting the images in your page.

       The  program  might not work because the path to Perl in the first
       line  of  fiximg.pl  is wrong. See if the path is correct by doing
       'which perl'  at  your Unix prompt. If it is not correct, you will
       have to edit the first line.

       Beware  of  so-called  Redirects;  in  our  case  our DocumentRoot 

              /vol/www/thalia/web-docs/

       was  redirected  to "/thalia". This caused errors when images were
       accessed  as  "/thalia/gifs/image.gif".  The  real  image would be

              /vol/www/thalia/web-docs/gifs/image.gif

       but the program would come up with the filename

              /vol/www/thalia/web-docs/thalia/gifs/image.gif

       We  solved  it  by making a link in the DocumentRoot directory, to
       itself,  with  the name "thalia" ("ln -s . thalia"). You have been
       warned.

ACKNOWLEDGMENTS
       Richie B. <richie\@morra.et.tudelft.nl> for the subroutine that
       fixes user-URL's starting with "/~".

AUTHOR
       Patrick Atoon <patricka\@cs.kun.nl>

EOF
}










