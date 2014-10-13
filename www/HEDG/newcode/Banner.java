/* Banner.java
   Sasha Chislenko, Madan Ramakrishnan

06/04/98 - Sasha
   - Changed price functions to use capital if requested
06/02/98 - Sasha
   - Changed CalcRate to use total values of clicks and displays
06/01/98 - Sasha
   - Changed capital handling
06/01/98 - Madan
   - Moved banner vs. category and banner vs. site counters
     into Banner class
06/01/98 - Sasha
   - Added capital handling
   - Trimmed long lines and converted tabs to spaces
05/27/98 - Sasha
   - Added initialization of num*Total
05/20/98 - Sasha
   - Changed bias ranges to average to global average
05/18/98 - Sasha

   - Added TARGET_BANNERS constant
   - Added ConstantPrice function
*/

public class Banner extends Agent {
/* The banner class is used to represent banner objects.  It is
   a subclass of Agent.
    CONSTANTS:
     MAX_START_BANNERS
      holds the initial inventory of a particular banner
     NUM_BANNERS
      total number of banners in bannerList array

    VARIABLES:
     numLeft
      stores the current inventory of a particular banner
     successRate
      stores the ratio of banner clicks to banner displays
    FUNCTIONS:
     Banner()
      The constructor function for the banner class; intializes all
      pertinent banner variables after creating each specific banner
      object
     GetPrice()
      Returns the current price of a particular banner (used in the
      economic and intermediate selection models)
     GetHyperEPrice()
      Returns the current price of a particular banner (used only in
      the Hypereconomy selection model)
     CalcRate()
      Updates the value of variable successRate
     MatchCategory()
      Finds first common category between the current banner and a
      particular site (which is a parameter)
     RestartBannerCount()
      Initializes banner inventory values
*/

  static final double TARGET_BANNERS =  (double)
           (MiscFunc.MIN_START_BANNERS + MiscFunc.MAX_START_BANNERS) * 0.5;

  int numLeft;
  int initNumLeft;
  double successRate;
  double capital;
  double banVsCatBias[]   = new double[MiscFunc.NUM_CATEGORIES];
  double banVsSiteBias[]  = new double[MiscFunc.NUM_SITES];
  double[] siteVsBanClick = new double[MiscFunc.NUM_SITES];
  double[] siteVsBanImp   = new double[MiscFunc.NUM_SITES];
  double[] banVsCatClick  = new double[MiscFunc.NUM_CATEGORIES];
  double[] banVsCatImp    = new double[MiscFunc.NUM_CATEGORIES];

  public double GetPricePositive(double use_capital) {
      double add_to_price = this.capital * MiscFunc.CAPITAL_FACTOR;
      double price = TARGET_BANNERS / this.numLeft  +  add_to_price * use_capital;
      if (price < 0.01) price = 0.01;
      return (price);
   }

  public double GetPrice(double use_capital) {
      double add_to_price = this.capital * MiscFunc.CAPITAL_FACTOR;
      if ( add_to_price > 1.0 )  add_to_price = 1.0;
      if ( add_to_price < -1.0 ) add_to_price = -1.0;
      return ( 3.0 - 2.0 * (double) this.numLeft / TARGET_BANNERS +
      add_to_price * use_capital);
   }

   public double GetFlexPrice() {
         return
         ( 1.0 +  (TARGET_BANNERS - (double) this.numLeft)
                                / ((double) this.numClicks * 0.01 + 4.0));
   }

   public double GetIntPrice (double use_capital) {
      double add_to_price = this.capital * MiscFunc.CAPITAL_FACTOR;
      if ( add_to_price > 2.0 )  add_to_price = 2.0;
      if ( add_to_price < -2.0 ) add_to_price = -2.0;
     return (3.0 - 2.0 * (double) this.numLeft / TARGET_BANNERS + 1.0
            - MiscFunc.GLOBAL_CLICK_RATE /
      ((this.numClicksTotal + 1.0 ) /
      (this.numDisplaysTotal + 1.0 / MiscFunc.GLOBAL_CLICK_RATE))
      + add_to_price * use_capital);
   }


  public double GetHyperEPrice(double use_capital)  {
      double add_to_price = this.capital * MiscFunc.CAPITAL_FACTOR;
      if ( add_to_price > 1.0 )  add_to_price = 1.0;
      if ( add_to_price < -1.0 ) add_to_price = -1.0;
      return (1.0 +  (TARGET_BANNERS - (double) this.numLeft)
                     / ((double) this.numClicks * 0.01 + 4.0) + 1.0
        - MiscFunc.GLOBAL_CLICK_RATE /
        ((this.numClicks + 1.0 ) / (this.numDisplays + 1.0 / MiscFunc.GLOBAL_CLICK_RATE))
        + add_to_price * use_capital);
  }

  public double ConstantPrice() {
      return 1.0;
  }

  public double GetFlexIntPrice() {

     return (1.0 +  (TARGET_BANNERS - (double) this.numLeft)
                     / ((double) this.numClicks * 0.01 + 4.0) + 1.0
        - MiscFunc.GLOBAL_CLICK_RATE /
        ((this.numClicks + 1.0 ) / (this.numDisplays + 1.0 / MiscFunc.GLOBAL_CLICK_RATE)));
   }

  public int MatchCategory(Site tempSite) {
    int k,l;
    int returnVal = 0;
    boolean foundMatch = false;

    for (k=0; k < tempSite.numCategoriesIn && !foundMatch; k++) {
      for (l=0; l < this.numCategoriesIn && !foundMatch; l++) {
        foundMatch = (this.categoriesIn[l] == tempSite.categoriesIn[k]);
        returnVal = l;
      }
    }
    if (foundMatch)
      return this.categoriesIn[returnVal];  // if match, return category
    else
      return -3;   // no match!!!
  }

  public void CalcRate() {
    if (this.numDisplays == 0)
      this.successRate = 0.0;
    else
      this.successRate = (double)
        Math.floor((double) this.numClicksTotal / this.numDisplaysTotal *
                            MiscFunc.PREC_FACTOR) / MiscFunc.PREC_FACTOR;
  }

  public void RestartBannerCount() {
    int i;
    numLeft = initNumLeft;
    this.successRate = 0.0;
    this.numDisplays = 0.0;
    this.numClicks   = 0.0;
    this.numClicksTotal = 0.0;
    this.numDisplaysTotal = 0.0;
    this.capital = 0.0;

    for (i=0; i < MiscFunc.NUM_CATEGORIES; i++) {
      banVsCatClick[i] = 0.0;
      banVsCatImp[i] = 0.0;
    }
    for (i=0; i < MiscFunc.NUM_SITES; i++) {
      siteVsBanClick[i] = 0.0;
      siteVsBanImp[i] = 0.0;
    }
  }

  public Banner(Site siteForBanner) {
    super();
    int i;
    this.numCategoriesIn = siteForBanner.numCategoriesIn;
    for (i=0; i < siteForBanner.numCategoriesIn; i++) {
      this.categoriesIn[i] = siteForBanner.categoriesIn[i];
    }
    bias = miscFunctions.RandomVal(MiscFunc.MIN_BAN_BIAS,MiscFunc.MAX_BAN_BIAS);
    initNumLeft = miscFunctions.RandomVal(MiscFunc.MIN_START_BANNERS,MiscFunc.MAX_START_BANNERS + 1);
    for (i=0; i < MiscFunc.NUM_CATEGORIES; i++) {
      banVsCatBias[i] = miscFunctions.RandomVal(MiscFunc.MIN_BAN_VS_CAT_BIAS,
                            MiscFunc.MAX_BAN_VS_CAT_BIAS);
    }
    for (i=0; i < MiscFunc.NUM_SITES; i++) {
      banVsSiteBias[i] = miscFunctions.RandomVal(MiscFunc.MIN_BAN_VS_SITE_BIAS,
                             MiscFunc.MAX_BAN_VS_SITE_BIAS);
    }
  }
}
