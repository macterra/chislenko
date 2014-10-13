/* HyperE.java

   Sasha Chislenko, Madan Ramakrishnan

 Modification history:
 ---------------------
 06/05 - Sasha
         Changed utility function for hypereconomy
 06/04 - Madan
       - added bias amplitudes in Agent.java
 06/04 - Sasha
       - fixed bug in hypereconomy utility calculation, marked in code
       - added category total click/display variables
       - Changed calls to price functions to take capital
       - banner price is now output properly for each model (was economy everywhere)
         I fixed it two days ago but it got lost.
       - improved banner.dat printing
 06/03 - Sasha
       - restored yesterday's changes that didn't get here:
          - fixed click increments
          - use GetHyperEPrice now for hypereconomy
 06/03 - Madan
       - added category click/display data
 06/02 - Sasha
       - capital fixes are now all in banner.java, here only keep track of capital
 06/01 - Madan
       - Moved siteVsBan, siteVsCat, and banVsCat click and impression
         counters to Site and Banner classes
 06/01 - Sasha
       - renamed capital_factor variable into own_price
       - added actual capital tracking for banners and sites
       - converted tabs to spaces in text
 05/30 - Madan
       - Finished moving all constants to GENERATOR.PAR parameter file
 05/29 - Madan
       - Deleted NEW_FILE option; instead separated access generator
         into Generator.java
       - Can now read constants from GENERATOR.PAR file; must still finish
         transferring constants from Site.java, Agent.java, and Category.java
 05/28 - Sasha
       - Improved printing
       - moved implementation plans to hypereconomy_plans.txt
 05/28 - Madan
       - Added NEW_FILE option; uses old file if false, otherwise creates
         new file
 05/25 - Sasha
       - added diagnostic prints, comments
       - fixed category fluctuations, now seem to work
       - births and deaths still flaky
       - see also changes in other files
 05/24 - Sasha
       - more fixes in births and deaths
       - added birth and death times to BANNER.DAT reports
       - added increment function calls for clicks and impressions
       - added variable category biases (see also category.java, MiscFunc.java)
         All of the above needs careful testing and, probably, fixing
       - updated list of planned changes
 05/22 - Sasha
       - fixes to births and deaths
         accesses are now not generated for non-alive sites
         and banners are ignored during that time.
       - added diagnostic prints to access generator
 05/22 - Madan
       - Added sites "births" & "deaths"
 05/20 - Sasha
       - removed duplicate computation of prices, now picks first best
       - moved site capital computation out of the inner banner loop
       - some print improvements
 05/18 - Sasha
       - Put model types and names in arrays
       - some print improvements
 05/18 - Sasha
       - added restart banner count before writing banner.dat
       - removed some old comments
       - also, see changes in banner.java
 05/18 - Madan
       - added site-to-cat/banner-cat biases
       - added banner-to-site bias
       - added cumulative noShow data
       - now have variable initial inventory for banners
*/

import java.io.*;
import java.util.StringTokenizer;
import java.lang.*;

public class HyperE {
/*
    CONSTANTS:
    VARIABLES:
     currentSite
      Holds ID # of site currently being used
     bannerToShow
      Holds ID # of banner currently being used
     matchingCategory
      Holds ID # of common category between
      a particular site and banner
    FUNCTIONS:

*/

  public static final int    RANDOM_ID       = 0;
  public static final int    SCHEDULER_ID    = 1;
  public static final int    ECONOMY_ID      = 2;
  public static final int    INTERMEDIATE_ID = 3;
  public static final int    HYPERECONOMY_ID = 4;
  // number of different optimization models
  public static final int    NUM_MODELS      = 4;
  // Run random model until this point
  public static final String[]  MODEL_NAMES = {"RAN", "SCH","ECO","INT","HEC"};

//------------------------------------------------------------------------
static long RunModel(int model_id, Site[] siteList,
                     Banner[] bannerList, Category categories)
{
  // Initialize miscellaneous function object
  MiscFunc miscFunctions;
  miscFunctions = new MiscFunc();
  String modelExt = MODEL_NAMES [model_id];

  String thisLine;

  long h,
       totalClicks = 0,
       currentTime,
       smoothedClicks = 0;
  int i,
      j,
      k,
      currentSite,
      matchingCategory,
      bannerToShow,
      totalNoShows = 0,
      bestBannerIndex;
  double price         = 0.0,
         utility       = 0.0,
         own_price     = 0.0,
         sort_factor   = 0.0,
         bestSortFactor,
         bestUtility,
         bestPrice,
         clickIncrement;

  int[] clicksPerTenThousand            = new int[1000];
  int[] noShowsPerTenThousand           = new int[1000];
  int[] cumulativeNoShowsPerTenThousand = new int[1000];
  int[] cumulativeclicksPerTenThousand  = new int[1000];

  for (j=0; j < MiscFunc.NUM_BANNERS; j++) {
    bannerList[j].RestartBannerCount();
  }

  for (j=0; j < MiscFunc.NUM_SITES; j++) {
    siteList[j].RestartSiteCount();
  }

  categories.RestartCatCount();

  try {
    // Open file ACCESS.DAT (which was created in ACCESS GENERATOR
    FileInputStream fin =  new FileInputStream("ACCESS.DAT");
    DataInputStream accessFile = new DataInputStream(fin);

    // Create a file ACCESS2.* which will store information on
    //  successful clicks
    FileOutputStream fout = new FileOutputStream("ACCESS2." + modelExt);
    PrintStream access2Output = new PrintStream(fout);

    for (h=0; h < MiscFunc.NUM_ACCESSES; h++) {
      thisLine = accessFile.readLine();
      String tempLine = thisLine;
      StringTokenizer tempTokenizer = new StringTokenizer(tempLine);

      // Read pertinent data from access file
      currentTime = Long.valueOf(tempTokenizer.nextToken()).longValue();
      currentSite = Integer.valueOf(tempTokenizer.nextToken()).intValue();

      if (h % 1000 == 0)
        System.out.println(h + "/" + MiscFunc.NUM_ACCESSES + "  " + modelExt);

      // For each eligible banner for the current site, calculate the
      // utility, price, and sort criteria; The method of price and
      // utility calculation depends on the particular model

      bestBannerIndex = -1;
      bestSortFactor  = -999999.0;
      bestPrice       = 999.0;
      bestUtility     = -999.0;

      // Compute data that doesn't depend on banner:
      price = 0.0;
      own_price = 0.0;
      utility = MiscFunc.GLOBAL_CLICK_RATE;

      switch (model_id) {
        case ECONOMY_ID:
          own_price = bannerList[currentSite].GetPrice(0.0);
          break;
        case INTERMEDIATE_ID:
          own_price = bannerList[currentSite].GetIntPrice(0.0);
          break;
        case HYPERECONOMY_ID:
          own_price = bannerList[currentSite].GetHyperEPrice(0.0);
          break;
      }

      //  Now go through banners and compute data related to them

      for (j=0; j < siteList[currentSite].numMatchingSites; j++) {
        bannerToShow = (int) siteList[currentSite].matchingSites[j][1];
        matchingCategory = (int) siteList[currentSite].matchingSites[j][2];

        if (bannerList[currentSite].birthTime <= currentTime &&
            bannerList[currentSite].deathTime > currentTime &&
            bannerList[currentSite].numLeft > 0 )
        {
          switch (model_id) {
            case RANDOM_ID:
              sort_factor = (double) bannerList[bannerToShow].numLeft;
              break;
            case SCHEDULER_ID:
              sort_factor = (double)
                  ((bannerList[bannerToShow].siteVsBanClick[currentSite] + 1.0) /
                  (bannerList[bannerToShow].siteVsBanImp[currentSite] + 1.0 /
                  ((siteList[currentSite].numClicks + 1.0) /
                  (siteList[currentSite].numDisplays + 1.0 / MiscFunc.GLOBAL_CLICK_RATE))));
              sort_factor = utility;
              break;
            case ECONOMY_ID:
              utility = (double)
                  ((bannerList[bannerToShow].siteVsBanClick[currentSite] + 1.0) /
                  (bannerList[bannerToShow].siteVsBanImp[currentSite] + 1.0 /
                  ((siteList[currentSite].numClicks + 1.0) /
                  (siteList[currentSite].numDisplays + 1.0 / MiscFunc.GLOBAL_CLICK_RATE))));

              price = (double) bannerList[bannerToShow].GetPrice(0.0);
              sort_factor = utility * (1.0 + own_price - price);
              break;
            case INTERMEDIATE_ID:
              utility = (double)
                  ((bannerList[bannerToShow].siteVsBanClick[currentSite] + 1.0) /
                   (bannerList[bannerToShow].siteVsBanImp[currentSite] +
                   ((1.0 + (double) siteList[currentSite].numClicks) *
                   (1.0 + (double) bannerList[bannerToShow].numClicks) / MiscFunc.GLOBAL_CLICK_RATE /
                   ((1.0 / MiscFunc.GLOBAL_CLICK_RATE + (double) siteList[currentSite].numDisplays) *
                   (1.0 / MiscFunc.GLOBAL_CLICK_RATE + (double) bannerList[bannerToShow].numDisplays)))));

              price = bannerList[bannerToShow].GetIntPrice(0.0);
              sort_factor =  utility * (1.0 + own_price - price);
              break;
            case HYPERECONOMY_ID:

              double cat_estimate = (categories.catClicks[matchingCategory] + 1.0 ) /
                      (categories.catDisplays[matchingCategory] + 1.0 / MiscFunc.GLOBAL_CLICK_RATE);

              double individual_to_cat_estimate =
                    (siteList[currentSite].siteVsCatClick[matchingCategory] +
                      bannerList[bannerToShow].banVsCatClick[matchingCategory] + 1.0) /
                     (siteList[currentSite].siteVsCatImp[matchingCategory] +
                     bannerList[bannerToShow].banVsCatImp[matchingCategory] + 1.0 /
                     cat_estimate);

              double individual_estimate = (bannerList[bannerToShow].siteVsBanClick[currentSite] + 1.0) /
                       (bannerList[bannerToShow].siteVsBanImp[currentSite] + 1.0 /
                       individual_to_cat_estimate);

              /* at the beginning stage, the above uses fallback to general data;
                 otherwise, adjust it for current category trend: */

            if (bannerList[bannerToShow].siteVsBanClick[currentSite] < 2)
                utility = individual_estimate;
              else  // now - average between global and adjusted
                utility = individual_estimate * cat_estimate * 0.5 * (1.0 + 1.0 /
                          categories.catClicksTotal[matchingCategory] *
                          categories.catDisplaysTotal[matchingCategory]);

              price = (double) bannerList[bannerToShow].GetHyperEPrice(0.0);

              sort_factor =  utility * (1.0 + own_price - price);
              break;
          } // end switch
        }  // done considering banner

      if (h < MiscFunc.MAX_RANDOM)  // ???  doesn't work now
      {
        utility = MiscFunc.GLOBAL_CLICK_RATE;
        price   = MiscFunc.GLOBAL_CLICK_RATE;
        sort_factor = 1.0;
        // bannerList[bannerToShow].numLeft = MAX_START_BANNERS;       // restore banner inventory
      }

      if (sort_factor > bestSortFactor && bannerList[bannerToShow].numLeft > 0) {
        bestSortFactor  = sort_factor;
        bestBannerIndex = j;
        bestUtility = utility;
        bestPrice=price;
      }
    }

        if (bestBannerIndex >= 0) {
           bannerToShow = (int) siteList[currentSite].matchingSites[bestBannerIndex][1];
       price       = bestPrice;
       utility     = bestUtility;
       sort_factor = bestSortFactor;
       matchingCategory = (int) siteList[currentSite].matchingSites[bestBannerIndex][2];

    // this print may not have some of its data now:
    if (utility > 0.9 || h < 10 || currentSite < 3  || bannerToShow < 3)
        {
      access2Output.println("Utility: " + (float) utility );
      access2Output.println("sitevsban: " +
            bannerList[bannerToShow].siteVsBanClick[currentSite] + "/" +
                    bannerList[bannerToShow].siteVsBanImp[currentSite]);
      access2Output.println("site: " + siteList[currentSite].numClicks + "/" +
            siteList[currentSite].numDisplays +
                " \t numLeft: " + bannerList[currentSite].numLeft);
      access2Output.println("banner: " + bannerList[bannerToShow].numClicks + "/" +
            bannerList[bannerToShow].numDisplays +
                " \t numLeft: " + bannerList[bannerToShow].numLeft);
      access2Output.println("catVsCat: " + categories.catClicks[matchingCategory]
                                 + "/" + categories.catDisplays[matchingCategory]);
      access2Output.println("price: " + price);
      access2Output.println("own_price: " + own_price);
      access2Output.println("sort factor: " + sort_factor);
      access2Output.println("Adaptive multiplier for category: " +
             (1.0 + categories.fluct_bias_amplitude[matchingCategory] *
               Math.sin ((double) currentTime /
               categories.fluct_bias_period[matchingCategory] +
             categories.fluct_bias_shift[matchingCategory])));
    }

      // Increment particular impression values
      bannerList[bannerToShow].numDisplaysTotal += 1.0;

      bannerList[bannerToShow].numDisplays =
        miscFunctions.incrementImpressions (bannerList[bannerToShow].numDisplays);
      siteList[currentSite].numDisplays =
        miscFunctions.incrementImpressions (siteList[currentSite].numDisplays);
      siteList[currentSite].siteVsCatImp[matchingCategory] =
        miscFunctions.incrementImpressions (siteList[currentSite].siteVsCatImp[matchingCategory]);
          bannerList[bannerToShow].banVsCatImp[matchingCategory] =
        miscFunctions.incrementImpressions (bannerList[bannerToShow].banVsCatImp[matchingCategory]);
      bannerList[bannerToShow].siteVsBanImp[currentSite] =
        miscFunctions.incrementImpressions (bannerList[bannerToShow].siteVsBanImp[currentSite]);
      categories.catDisplays[matchingCategory] =
        miscFunctions.incrementImpressions (categories.catDisplays[matchingCategory]);
      categories.catDisplaysTotal[matchingCategory] += 1.0;


      // Check to see if selected banner was clicked
      // If so, update relevant counter variables
      if (Math.random() <= MiscFunc.GLOBAL_CLICK_RATE *
         bannerList[bannerToShow].banVsSiteBias[currentSite] *
             bannerList[bannerToShow].bias *
             siteList[currentSite].bias *
             bannerList[bannerToShow].banVsCatBias[matchingCategory] *
             // variable factor here:
             (1.0 + categories.fluct_bias_amplitude[matchingCategory] *
               Math.sin ((double) currentTime /
               categories.fluct_bias_period[matchingCategory] +
                 categories.fluct_bias_shift[matchingCategory])) *
             siteList[currentSite].siteVsCatBias[matchingCategory] *
             categories.bias[matchingCategory])

      {
        access2Output.println(currentTime + "  " + currentSite
              + "    " + bannerToShow + " \t" + (float) utility + "  \t" +
              (float)  price + "  \t" +
              (float) sort_factor + " CLICKED");

           if (MiscFunc.ADAPTIVE_INVENTORY && h > MiscFunc.MAX_RANDOM) {
               bannerList[bannerToShow].numLeft--;
               bannerList[currentSite].numLeft++;
            }
        clickIncrement = 1.0;
        totalClicks++;

        bannerList[currentSite].capital  -= price;  // paid the price
        bannerList[bannerToShow].capital += price;  // got paid the price

        clicksPerTenThousand[(int) Math.floor(h / 10000)]++;

      }
      else {
        clickIncrement = 0.0;
        access2Output.println(currentTime + "  " + currentSite
              + "    "  + bannerToShow + " \t" + (float) utility
              + "  \t" + (float)  price + "  \t" +
              (float) siteList[currentSite].matchingSites[bestBannerIndex][0]);
          }

      siteList[currentSite].numClicks =
         miscFunctions.incrementClicks (siteList[currentSite].numDisplays,
         siteList[currentSite].numClicks, clickIncrement);

      bannerList[bannerToShow].numClicksTotal += clickIncrement;
      bannerList[bannerToShow].numClicks =
         miscFunctions.incrementClicks (bannerList[bannerToShow].numDisplays,
         bannerList[bannerToShow].numClicks, clickIncrement);

      siteList[currentSite].siteVsCatClick[matchingCategory] =
         miscFunctions.incrementClicks (siteList[currentSite].siteVsCatImp[matchingCategory],
         siteList[currentSite].siteVsCatClick[matchingCategory], clickIncrement);
      bannerList[bannerToShow].banVsCatClick[matchingCategory] =
         miscFunctions.incrementClicks (bannerList[bannerToShow].banVsCatImp[matchingCategory],
         bannerList[bannerToShow].banVsCatClick[matchingCategory], clickIncrement);
      bannerList[bannerToShow].siteVsBanClick[currentSite] =
         miscFunctions.incrementClicks (bannerList[bannerToShow].siteVsBanImp[currentSite],
         bannerList[bannerToShow].siteVsBanClick[currentSite], clickIncrement);
      categories.catClicks[matchingCategory] =
         miscFunctions.incrementClicks (categories.catDisplays[matchingCategory],
         categories.catClicks[matchingCategory], clickIncrement);
      categories.catClicksTotal[matchingCategory] += clickIncrement;

      bannerList[bannerToShow].CalcRate();
    }
    else {
      access2Output.println(currentTime + "  " + currentSite + "  " + "NOBANNER!");
      noShowsPerTenThousand[(int) Math.floor(h / 10000)]++;
    }
       }
      }
    catch (IOException e) {                          // 4
      System.out.println("failed to open file ACCESS.DAT");
      System.out.println("Error: " + e);
    }

    // Write banner data to file
    try {
      FileOutputStream fout =  new FileOutputStream("BANNER." + modelExt);
      PrintStream bannerOutput = new PrintStream(fout);
      bannerOutput.println
         ("ID   numLeft     bias   S.Rate  Clicks  Imps   Price  capital   Birth     Death       #catIn  categoriesIn");
            for (i = 0; i < MiscFunc.NUM_BANNERS; i++) {
              price= 0.0;
              switch (model_id) {
                  case ECONOMY_ID:
                       price = (double) bannerList[i].GetPrice(0.0);
                       break;
                  case INTERMEDIATE_ID:
                       price = (double) bannerList[i].GetIntPrice(0.0);
                       break;
                  case HYPERECONOMY_ID:
                       price = (double) bannerList[i].GetHyperEPrice(0.0);
               }

              bannerList[i].CalcRate();
              bannerOutput.print(i + "   \t" + bannerList[i].numLeft +
                " \t"   + bannerList[i].bias +
                "\t"    + bannerList[i].successRate +
                "\t"    + (int) bannerList[i].numClicksTotal   +
                "   \t" + (int)   bannerList[i].numDisplaysTotal +
                "  \t"  + (float) price +
                "\t"    + (int) bannerList[i].capital +
                "\t"    + bannerList[i].birthTime +
                "\t"    + bannerList[i].deathTime +
          "  \t" + bannerList[i].numCategoriesIn + "\t");

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

    //WRITE BANNER CLICKRATE TO FILE
    try {
      FileOutputStream fout =  new FileOutputStream("CLICKS." + modelExt);
      PrintStream clickOutput = new PrintStream(fout);
          clickOutput.println("CatN   numDisplays  numClicks successRate");
      for (i=0; i < MiscFunc.NUM_BANNERS; i++) {
        clickOutput.println(i + "\t" + bannerList[i].numDisplays + "\t"
              + (float) bannerList[i].numClicks + "\t" + bannerList[i].successRate);
      }
    }
    catch (IOException e) {
      System.out.println("Error opening file: " + e);
      System.exit(1);
    }

  // WRITE CLICKS PER TEN THOUSAND ACCESS DATA TO FILE
  try {
    FileOutputStream fout =  new FileOutputStream("BIGPIC." + modelExt);
    PrintStream clickOutput = new PrintStream(fout);

    cumulativeclicksPerTenThousand[0] = clicksPerTenThousand[0];
    for (j=0; j < Math.floor(MiscFunc.NUM_ACCESSES / 10000); j++) {
      if (j  > 0)
      cumulativeclicksPerTenThousand[j] =
        cumulativeclicksPerTenThousand[j-1] + clicksPerTenThousand[j];
      if (j > 9)
         smoothedClicks = (cumulativeclicksPerTenThousand[j] -
                             cumulativeclicksPerTenThousand[j-10] ) / 10;
      else
         smoothedClicks = cumulativeclicksPerTenThousand[j] / (j+1);

      clickOutput.println(j + "  " + clicksPerTenThousand[j] + " \t" +
       smoothedClicks + " \t " + cumulativeclicksPerTenThousand[j] / (j+1));
    }

  }
  catch (IOException e) {
    System.out.println("Error opening file: " + e);
    System.exit(1);
  }

  // WRITE TOTAL NUMBER OF CLICKS TO FILE
  try {
    FileOutputStream fout =  new FileOutputStream("TOTAL." + modelExt);
    PrintStream clickOutput = new PrintStream(fout);
    clickOutput.println(totalClicks);
  }
  catch (IOException e) {
    System.out.println("Error opening file: " + e);
    System.exit(1);
  }

  // WRITE TOTAL NUMBER OF BANNER NO SHOWS TO FILE
  try {
    FileOutputStream fout =  new FileOutputStream("NOSHOWS." + modelExt);
    PrintStream clickOutput = new PrintStream(fout);

    cumulativeNoShowsPerTenThousand[0] = noShowsPerTenThousand[0];
    for (j=0; j < Math.floor(MiscFunc.NUM_ACCESSES / 10000); j++) {
      if (j  > 0)
        cumulativeNoShowsPerTenThousand[j] =
        cumulativeNoShowsPerTenThousand[j-1] + noShowsPerTenThousand[j];
      if (j > 9)
        smoothedClicks = (cumulativeNoShowsPerTenThousand[j] -
                             cumulativeNoShowsPerTenThousand[j-10] ) / 10;
      else
        smoothedClicks = cumulativeNoShowsPerTenThousand[j] / (j+1);

      clickOutput.println(j + "  " + noShowsPerTenThousand[j] + " \t" +
       smoothedClicks + " \t " + cumulativeNoShowsPerTenThousand[j] / (j+1));
    }

  }
  catch (IOException e) {
    System.out.println("Error opening file: " + e);
    System.exit(1);
  }

  // return total number of clicks in this model
  return totalClicks;
}
//----------------------------------------------------------------------------
public static void main(String argv[]) {
  // Declare all necessary variables (descriptions of variables above)
  MiscFunc miscFunctions;
  miscFunctions = new MiscFunc();

  long h,currentTime;
  int model_id;
  int i,j,k,tempVal;
  int currentSite,bannerToShow,matchingCategory;

  boolean sitesCheck[] = new boolean[MiscFunc.NUM_SITES];

  String thisLine;

  Site siteList[]      = new Site[MiscFunc.NUM_SITES];
  Banner bannerList[]  = new Banner[MiscFunc.NUM_BANNERS];
  int tempSiteFreq[]   = new int[MiscFunc.NUM_SITES];
  int tempSiteFreqMax  = 0;
  long numModelClicks[] = {0,0,0,0,0};



  // Initialize site array
  for (i = 0; i < siteList.length; i++) {
    siteList[i] = new Site();
  }

  // Initialize category info
  Category categories = new Category(siteList);

  // Initialize Banner array
  for (i = 0; i < bannerList.length; i++) {
    bannerList[i] = new Banner(siteList[i]);
  }


  System.out.println("About to read in site data");
  // Read in site data
  try {
    FileInputStream fin =  new FileInputStream("SITE.DAT");
    DataInputStream accessFile = new DataInputStream(fin);

    thisLine = accessFile.readLine();
    for (j=0; j < MiscFunc.NUM_SITES; j++) {
      thisLine = accessFile.readLine();
      String tempLine = thisLine;
      StringTokenizer tempTokenizer = new StringTokenizer(tempLine);

      tempVal                = Integer.valueOf(tempTokenizer.nextToken()).intValue();
      siteList[j].intFreq    = Integer.valueOf(tempTokenizer.nextToken()).intValue();
      siteList[j].birthTime  = Integer.valueOf(tempTokenizer.nextToken()).intValue();
      siteList[j].deathTime  = Integer.valueOf(tempTokenizer.nextToken()).intValue();
      siteList[j].frequency  = Double.valueOf(tempTokenizer.nextToken()).doubleValue();
      siteList[j].bias       = Double.valueOf(tempTokenizer.nextToken()).doubleValue();
      siteList[j].biasShift  = Double.valueOf(tempTokenizer.nextToken()).doubleValue();
      siteList[j].biasPeriod = Double.valueOf(tempTokenizer.nextToken()).doubleValue();
      siteList[j].biasAmplitude = Double.valueOf(tempTokenizer.nextToken()).doubleValue();
      siteList[j].numCategoriesIn = Integer.valueOf(tempTokenizer.nextToken()).intValue();
      for (k=0; k < siteList[j].numCategoriesIn; k++) {
        siteList[j].categoriesIn[k] = Integer.valueOf(tempTokenizer.nextToken()).intValue();
      }
    }
  }
  catch (IOException e) {
    System.out.println("Error opening file: SITE.DAT");
  }

  System.out.println("Finished with SITE.DAT; Reading SITEVSCAT.DAT");
  // Read in site vs. category bias data
  try {
    FileInputStream fin =  new FileInputStream("SITEVSCAT.DAT");
    DataInputStream accessFile = new DataInputStream(fin);
    thisLine = accessFile.readLine();
    for (j=0; j < MiscFunc.NUM_SITES; j++) {
      thisLine = accessFile.readLine();
      String tempLine = thisLine;
      StringTokenizer tempTokenizer = new StringTokenizer(tempLine);

      tempVal = Integer.valueOf(tempTokenizer.nextToken()).intValue();
      for (k=0; k < MiscFunc.NUM_CATEGORIES; k++) {
        siteList[j].siteVsCatBias[k] = Double.valueOf(tempTokenizer.nextToken()).doubleValue();
      }
    }
  }
  catch (IOException e) {
    System.out.println("Error opening file: SITEVSCAT.DAT");
  }

  System.out.println("Finished with SITEVSCAT.DAT; Reading CATEGORY.DAT");
  // Read in category data
  try {
    FileInputStream fin =  new FileInputStream("CATEGORY.DAT");
    DataInputStream accessFile = new DataInputStream(fin);
    thisLine = accessFile.readLine();

    for (j=0; j < MiscFunc.NUM_CATEGORIES; j++) {
      thisLine = accessFile.readLine();
      String tempLine = thisLine;
      StringTokenizer tempTokenizer = new StringTokenizer(tempLine);

      tempVal                            = Integer.valueOf(tempTokenizer.nextToken()).intValue();
      categories.bias[j]                 = Double.valueOf(tempTokenizer.nextToken()).doubleValue();
      categories.fluct_bias_amplitude[j] = Double.valueOf(tempTokenizer.nextToken()).doubleValue();
      categories.fluct_bias_period[j]    = Double.valueOf(tempTokenizer.nextToken()).doubleValue();
      categories.fluct_bias_shift[j]     = Double.valueOf(tempTokenizer.nextToken()).doubleValue();
      categories.numSitesPerCat[j]       = Integer.valueOf(tempTokenizer.nextToken()).intValue();
      categories.sitesPerCat[j][MiscFunc.NUM_SITES] = categories.numSitesPerCat[j];
      for (k=0; k < categories.numSitesPerCat[j]; k++) {
        categories.sitesPerCat[j][k] = Integer.valueOf(tempTokenizer.nextToken()).intValue();
      }
    }
  }
  catch (IOException e) {
    System.out.println("Error opening file: CATEGORY.DAT");
  }

  System.out.println("Finished with CATEGORY.DAT; Reading BANNER.DAT");
  // Read in banner data
  try {
    FileInputStream fin =  new FileInputStream("BANNER.DAT");
    DataInputStream accessFile = new DataInputStream(fin);
    thisLine = accessFile.readLine();

    for (j=0; j < MiscFunc.NUM_BANNERS; j++) {
      thisLine = accessFile.readLine();
      String tempLine = thisLine;
      StringTokenizer tempTokenizer = new StringTokenizer(tempLine);

      tempVal                   = Integer.valueOf(tempTokenizer.nextToken()).intValue();
      bannerList[j].initNumLeft = Integer.valueOf(tempTokenizer.nextToken()).intValue();
      bannerList[j].bias        = Double.valueOf(tempTokenizer.nextToken()).doubleValue();
      bannerList[j].biasShift   = Double.valueOf(tempTokenizer.nextToken()).doubleValue();
      bannerList[j].biasPeriod  = Double.valueOf(tempTokenizer.nextToken()).doubleValue();
      bannerList[j].biasAmplitude  = Double.valueOf(tempTokenizer.nextToken()).doubleValue();
      bannerList[j].birthTime   = Integer.valueOf(tempTokenizer.nextToken()).intValue();
      bannerList[j].deathTime   = Integer.valueOf(tempTokenizer.nextToken()).intValue();
      bannerList[j].numCategoriesIn = Integer.valueOf(tempTokenizer.nextToken()).intValue();
      for (k=0; k < bannerList[j].numCategoriesIn; k++) {
        bannerList[j].categoriesIn[k] = Integer.valueOf(tempTokenizer.nextToken()).intValue();
      }
    }
  }
  catch (IOException e) {
    System.out.println("Error opening file: BANNER.DAT");
  }

  System.out.println("Finished with BANNER.DAT; Reading BANVSCAT.DAT");
  // Read in banner vs. category bias data
  try {
    FileInputStream fin =  new FileInputStream("BANVSCAT.DAT");
    DataInputStream accessFile = new DataInputStream(fin);
    thisLine = accessFile.readLine();
    for (j=0; j < MiscFunc.NUM_BANNERS; j++) {
      thisLine = accessFile.readLine();
      String tempLine = thisLine;
      StringTokenizer tempTokenizer = new StringTokenizer(tempLine);

      tempVal = Integer.valueOf(tempTokenizer.nextToken()).intValue();
      for (k=0; k < MiscFunc.NUM_CATEGORIES; k++) {
        bannerList[j].banVsCatBias[k] = Double.valueOf(tempTokenizer.nextToken()).doubleValue();
      }
    }
  }
  catch (IOException e) {
    System.out.println("Error opening file: BANVSCAT.DAT");
  }

  System.out.println("Finished with BANVSCAT.DAT; Reading BANVSSITE.DAT");
  // Read in banner vs. site bias data
  try {
    FileInputStream fin =  new FileInputStream("BANVSSITE.DAT");
    DataInputStream accessFile = new DataInputStream(fin);
    thisLine = accessFile.readLine();
    for (j=0; j < MiscFunc.NUM_BANNERS; j++) {
      thisLine = accessFile.readLine();
      String tempLine = thisLine;
      StringTokenizer tempTokenizer = new StringTokenizer(tempLine);

      tempVal = Integer.valueOf(tempTokenizer.nextToken()).intValue();
      for (k=0; k < MiscFunc.NUM_SITES; k++) {
        bannerList[j].banVsSiteBias[k] = Double.valueOf(tempTokenizer.nextToken()).doubleValue();
      }
    }
  }
  catch (IOException e) {
    System.out.println("Error opening file: BANVSSITE.DAT");
  }

  for (j=0; j < MiscFunc.NUM_BANNERS; j++) {
    bannerList[j].RestartBannerCount();
  }
  System.out.println("Finished with BANVSSITE.DAT; Restarted banner counts");
  System.out.println("Finding all matching banners for all sites");

  // Find all matching banners for all sites
  for (i=0; i < MiscFunc.NUM_SITES; i++) {
    for (j=0; j < MiscFunc.NUM_SITES; j++) {
      sitesCheck[j] = false;
     }
     sitesCheck[i] = true;
     // This is really hard to understand; comments???

    for (j=0; j < siteList[i].numCategoriesIn; j++) {
      for (k=0; k < categories.sitesPerCat[siteList[i].categoriesIn[j]][MiscFunc.NUM_SITES]; k++) {
        if (!sitesCheck[categories.sitesPerCat[siteList[i].categoriesIn[j]][k]])
        {
          sitesCheck[categories.sitesPerCat[siteList[i].categoriesIn[j]][k]] = true;
          siteList[i].matchingSites[siteList[i].numMatchingSites][1] = (double)
                           categories.sitesPerCat[siteList[i].categoriesIn[j]][k];
          siteList[i].matchingSites[siteList[i].numMatchingSites][2] = (double)
                           bannerList[(int)
          siteList[i].matchingSites[siteList[i].numMatchingSites][1]].MatchCategory(siteList[i]);
          siteList[i].numMatchingSites++;
        }
      }
    }
  }

  System.out.println("About to run specified models");
  if (argv.length == 0) {
    // if no command line arguments, run all models
    for (model_id=0; model_id <= NUM_MODELS; model_id++) {
      numModelClicks[model_id] = RunModel(model_id,siteList,bannerList,categories);
    }
  }
  else {
    // depending on command line arguments, only run particular models
    for (i=0; i < argv.length; i++) {
      model_id = Integer.valueOf(argv[i]).intValue();
      if (model_id >= 0 && model_id <= NUM_MODELS) {
        numModelClicks[model_id] = RunModel(model_id, siteList,bannerList,categories);
      }
    }
  }

  // WRITE FINAL CLICK PERCENTAGES
  try {
    FileOutputStream fout   = new FileOutputStream("CLICK.TOT");
    PrintStream clickOutput = new PrintStream(fout);
    double perc;
    for (model_id=0; model_id <= NUM_MODELS; model_id++) {
      perc = (double) Math.floor((double) numModelClicks[model_id] /
                      MiscFunc.NUM_ACCESSES * MiscFunc.PREC_FACTOR) / MiscFunc.PREC_FACTOR;
      clickOutput.println(MODEL_NAMES[model_id] + "\t" +
                      numModelClicks[model_id] + " \t" +
                      MiscFunc.NUM_ACCESSES + "\t" + perc);
    }
  }
  catch (IOException e) {
    System.out.println("Error opening file CLICK.TOT: " + e);
    System.exit(1);
  }
}
}
