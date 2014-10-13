/* Generator.java

   Sasha Chislenko, Madan Ramakrishnan

Modifications history:

06/11/98  - Sasha
          - Time step is now constant, that fixes the problem of births and deaths
            beyond the time range
06/09/98  - Madan
          - Implemented variable site frequency
          - Fixed a bug in reporting access data, related to births and deaths
06/08/98  - Sasha
          - Renamed category fluct_bias parameters into biasXXX

*/

import java.io.*;
import java.util.StringTokenizer;
import java.lang.*;

public class Generator {
/* The generator class is used to generate access data files
   (along with site, banner, and category data) to be used
   by HyperE banner selection models
    VARIABLES:
     h,i,j,k
      various loop control variables
     currentSite
      used to store a pertinent site ID number (the current one
      being acted upon in some manner, depends on context)
     bannerToShow
      used in same way as currentSite, except used with banner ID
      numbers
     matchingCategory
      used in similar fashion to currentSite and bannerToShow
      (now a category ID number, usually between the currentSite
      and bannerToShow)
     siteList[]
      an array of objects of the Site class (stores all
      pertinent site data)
     bannerList[]
      an array of objects of the Banner class (stores all
      pertinent banner data)
     catList[]
      an array of objects of the Category class (stores all
      pertinent category data)

    FUNCTIONS:
     main()
      the only function in the class; use is pretty self-explanatory 
  
*/

//----------------------------------------------------------------------------
public static void main(String argv[]) {
  // Declare all necessary variables (descriptions of variables above)

  // Initialize miscellaneous function object
  MiscFunc miscFunctions;
  miscFunctions = new MiscFunc();

  long h,currentTime;
  int i,j,k;
  int currentSite,bannerToShow,matchingCategory;

  Site siteList[]      = new Site    [MiscFunc.NUM_SITES];
  Banner bannerList[]  = new Banner  [MiscFunc.NUM_BANNERS];
  Category catList[]   = new Category[MiscFunc.NUM_CATEGORIES];

  // Initialize site array
  for (i = 0; i < siteList.length; i++) {
    siteList[i] = new Site();
  }

  // Initialize category info
  for (i = 0; i < catList.length; i++) {
    catList[i] = new Category();
  }

  // Initialize sitesPerCat data
  for (i=0; i < MiscFunc.NUM_SITES; i++) {
    for (j=0; j < siteList[i].numCategoriesIn; j++) {
      catList[siteList[i].categoriesIn[j]].sitesPerCat[catList[siteList[i].categoriesIn[j]].numSitesPerCat] = i;
      catList[siteList[i].categoriesIn[j]].numSitesPerCat++;
    }
  }

  // Initialize Banner array
  for (i = 0; i < bannerList.length; i++) {
    bannerList[i] = new Banner(siteList[i]);
  }

  // ACCESS GENERATOR

  // set up sites that are born and sites that are born in the middle of run
  for (j=0; j < MiscFunc.NUM_SITES_BORN; j++) {
    do {
      currentSite = miscFunctions.RandomVal(0,MiscFunc.NUM_SITES);
      System.out.println ("currentSite: " + currentSite + "  birthTime: " +
                          siteList[currentSite].birthTime);
    } while (siteList[currentSite].birthTime != 0);

    siteList[currentSite].birthTime =
      miscFunctions.RandomVal(1,MiscFunc.TIME_STEP * MiscFunc.NUM_ACCESSES);
    bannerList[currentSite].birthTime = siteList[currentSite].birthTime;
  }

  System.out.println ("Finished setting up sites that get born in the middle of run");

  for (j=0; j < MiscFunc.NUM_SITES_DEAD; j++) {
    do {
      currentSite = miscFunctions.RandomVal(0,MiscFunc.NUM_SITES);
    } while (siteList[currentSite].deathTime != 9999999);
    siteList[currentSite].deathTime =
              miscFunctions.RandomVal(siteList[currentSite].birthTime,
                                      MiscFunc.TIME_STEP * MiscFunc.NUM_ACCESSES);
    System.out.println ("currentSite: " + currentSite + "  deathTime: " +
                       siteList[currentSite].deathTime);
    bannerList[currentSite].deathTime = siteList[currentSite].deathTime;
  }

  System.out.println ("Finished setting up sites that die in the middle of run");

  // generate ACCESS.DAT file
  try {
    FileOutputStream fout =  new FileOutputStream("ACCESS.DAT");
    PrintStream accessOutput = new PrintStream(fout);

    currentTime = 0;
    h = 0;
    while (h < MiscFunc.NUM_ACCESSES) {
      if (h % 1000 == 0)
        System.out.println("ACC. GENERATOR: " + h + "/" + MiscFunc.NUM_ACCESSES);
      currentTime += (long) MiscFunc.TIME_STEP;
// above was:      currentTime += (long) miscFunctions.RandomVal(0,MiscFunc.TIME_STEP);

      do {
        // find random site for current access
        currentSite = miscFunctions.RandomVal(0,MiscFunc.NUM_SITES);
        siteList[currentSite].discount = 0.0;
        
        // calculate current discount value for each site
        //  (based on current time)
        for (j=0; j < siteList[currentSite].numCategoriesIn; j++) {
          siteList[currentSite].discount +=
             catList[siteList[currentSite].categoriesIn[j]].CalcCatFreq(currentTime);
        }
        siteList[currentSite].discount = siteList[currentSite].discount /
                                        (double) siteList[currentSite].numCategoriesIn;

        // loop execution continues until valid site is found; that is,
        // site has been "born", site has not "died", and the discount
        // value (a measure of frequency) is greater than a random
        // dice roll
      } while (siteList[currentSite].birthTime > currentTime ||
             siteList[currentSite].deathTime < currentTime ||
             siteList[currentSite].discount < Math.random());

      // once valid site is found, counter is incremented and data
      // written to file
      h++;
      siteList[currentSite].intFreq++;
      accessOutput.println(currentTime + "   " + currentSite);
    }
  } // end try
  catch (IOException e) {
    System.out.println("Error opening file ACCESS.DAT");
    System.exit(1);
  }

  System.out.println("Finished generating accesses");

  // Initialize frequency values of Site
  for (i = 0; i < siteList.length; i++) {
    siteList[i].frequency = (double)
            Math.floor(MiscFunc.PREC_FACTOR * siteList[i].intFreq /
                       MiscFunc.NUM_ACCESSES) / MiscFunc.PREC_FACTOR;
  }

  System.out.println("Finished with frequencies");

  // initializes all banner click and impression counters
  for (j=0; j < MiscFunc.NUM_BANNERS; j++) {
    bannerList[j].RestartBannerCount();
  }

  System.out.println ("Restarted banner counts; About to write SITE.DAT");

  // Write site data to file
  try {
    FileOutputStream fout =  new FileOutputStream("SITE.DAT");
    PrintStream siteOutput = new PrintStream(fout);
    siteOutput.println("ID" + "\t" +
                       "IntFreq" + "\t" +
                       "birth" + "\t" +
                       "death" + "\t" +
                       "freq" + "\t" +
                       "bias" + "\t" +
                       "bShift" + "\t" +
                       "bPeriod" + "\t" +
                       "bAmp" + "\t" +
                       "#catIn" + "\t" +
                       "catIn");

    for (i = 0; i < MiscFunc.NUM_SITES; i++) {
      siteOutput.print(i + "\t" +
                       siteList[i].intFreq + "\t" +
                       siteList[i].birthTime + "\t" +
                       siteList[i].deathTime + "\t" +
                       siteList[i].frequency + "\t" +
                       siteList[i].bias + "\t" +
                       siteList[i].biasShift + "\t" +
                       siteList[i].biasPeriod + "\t" +
                       siteList[i].biasAmplitude + "\t" +
                       siteList[i].numCategoriesIn + "  ");
      for (j=0; j < siteList[i].numCategoriesIn; j++) {
        siteOutput.print(siteList[i].categoriesIn[j] + "\t");
      }
      siteOutput.println("");
    }
  }
  catch (IOException e) {
    System.out.println("Error opening file: SITE.DAT " + e);
  }

  System.out.println("Wrote SITE.DAT; about to write SITEVSCAT.DAT");
  // Write site vs. category biases to file
  try {
    FileOutputStream fout =  new FileOutputStream("SITEVSCAT.DAT");
    PrintStream siteOutput = new PrintStream(fout);
    siteOutput.println("siteID   (site are rows, categories are columns)");
    for (i = 0; i < MiscFunc.NUM_SITES; i++) {
      siteOutput.print(i);
      for (j=0; j < MiscFunc.NUM_CATEGORIES; j++) {
        siteOutput.print("\t" + siteList[i].siteVsCatBias[j]);
      }
      siteOutput.println("");
    }
  }
  catch (IOException e) {
    System.out.println("Error opening file: SITEVSCAT.DAT " + e);
  }

  // Write general banner data to file
  System.out.println("Finished with SITEVSCAT.DAT; about to write BANNER.DAT");

  try {
    FileOutputStream fout =  new FileOutputStream("BANNER.DAT");
    PrintStream bannerOutput = new PrintStream(fout);
    bannerOutput.println("ID" + "\t" +
                         "numLeft" + "\t" +
                         "bias" + "\t" +
                         "bShift" + "\t" +
                         "bPeriod" + "\t" +
                         "bAmp" + "\t" +
                         "birth" + "\t" +
                         "death" + "\t" +
                         "#catIn" + "\t" +
                         "catIn");

    for (i = 0; i < MiscFunc.NUM_BANNERS; i++)
    {
      bannerOutput.print(i + "\t" + bannerList[i].numLeft +
                             "\t" + bannerList[i].bias +
                             "\t" + bannerList[i].biasShift +
                             "\t" + bannerList[i].biasPeriod +
                             "\t" + bannerList[i].biasAmplitude +
                             "\t" + bannerList[i].birthTime +
                             "\t" + bannerList[i].deathTime +
                             "\t" + bannerList[i].numCategoriesIn +
                             "\t");
      for (j=0; j < bannerList[i].numCategoriesIn; j++) {
        bannerOutput.print(bannerList[i].categoriesIn[j] + " ");
      }
      bannerOutput.println("");
    }
  }
  catch (IOException e) {
    System.out.println("Error opening file: " + e);
    System.exit(1);
  }

  // Write banner vs category bias data to file
  System.out.println("Finished with BANNER.DAT; " +
                     "Now writing on BANVSCAT.DAT");

  try {
    FileOutputStream fout =  new FileOutputStream("BANVSCAT.DAT");
    PrintStream bannerOutput = new PrintStream(fout);
    bannerOutput.println("BannerID (Banners are rows, categories are columns)");

    for (i = 0; i < MiscFunc.NUM_BANNERS; i++)
    {
      bannerOutput.print(i);
      for (j=0; j < MiscFunc.NUM_CATEGORIES; j++) {
        bannerOutput.print("\t" + bannerList[i].banVsCatBias[j]);
      }
      bannerOutput.println("");
    }
  }
  catch (IOException e) {
    System.out.println("Error opening file: " + e);
    System.exit(1);
  }

  // Write banner vs site bias data to file
  System.out.println("Finished with BANVSCAT.DAT; " +
                     "Now writing on BANVSSITE.DAT");

  try {
    FileOutputStream fout =  new FileOutputStream("BANVSSITE.DAT");
    PrintStream bannerOutput = new PrintStream(fout);
    bannerOutput.println("BannerID (Banners are rows, sites are columns)");

    for (i = 0; i < MiscFunc.NUM_BANNERS; i++)
    {
      bannerOutput.print(i);
      for (j=0; j < MiscFunc.NUM_SITES; j++) {
        bannerOutput.print("\t" + bannerList[i].banVsSiteBias[j]);
      }
      bannerOutput.println("");
    }
  }
  catch (IOException e) {
    System.out.println("Error opening file: " + e);
    System.exit(1);
  }

  // Write category data to file
  System.out.println("Finished with BANVSSITE.DAT; about to write CATEGORY.DAT");
  try {
    FileOutputStream fout =  new FileOutputStream("CATEGORY.DAT");
    PrintStream catOutput = new PrintStream(fout);
    catOutput.println("CatID" + "\t" +
                      "bias" + "\t" +
                      "bAmp" + "\t" +
                      "bPeriod" + "\t" +
                      "bShift" + "\t" +
                      "#sitesIn" + "\t" +
                      "sitesIn");
    for (i = 0; i < MiscFunc.NUM_CATEGORIES; i++) {
      catOutput.print(i + " \t" +
                     catList[i].bias + "\t" +
                     catList[i].biasAmplitude + "\t" +
                     catList[i].biasPeriod + "\t" +
                     catList[i].biasShift + "\t" +
                     catList[i].numSitesPerCat + "\t");
      for (j=0; j < catList[i].numSitesPerCat; j++) {
        catOutput.print(catList[i].sitesPerCat[j] + " ");
      }
      catOutput.println("");
    }
  }
  catch (IOException e) {
    System.out.println("Error opening file: " + e);
    System.exit(1);
  }
  System.out.println("Finished with CATEGORY.DAT; Done with Generator");
}
}
