/* Site.java
   Sasha Chislenko, Madan Ramakrishnan

   06/08/98 - Madan
     - added discount (used in variable frequency access model)
   06/08/98 - Madan
     - replaced matchingSites with 2 parallel arrays
       (matchingBanners & matchingCategories)
   06/01/98 - Madan
     - added RestartSiteCount method
*/


public class Site extends Agent {
/* The site class is used to represent site objects.  It is a
   subclass of Agent
    VARIABLES:
     frequency
      stores the ratio of # of times a site was accessed to
      the total number of accesses
     intFreq
      stores the # of times a particular site was accessed
     numMatchingSites
      the number of banners that can be shown on a particular
      site (i.e., the number of banners with at least one
      matching category)
     matchingBanners[]
      array holding ID numbers for all banner eligible for
      a particular site (used in conjunction with 
      matchingCategories[])
     matchingCategories[]
      array holding matching category between each of the
      eligible banners (stored in a parallel array 
      matchingBanners[]) and a particular site
     siteVsCatBias[]
      bias values between particular site and each category
     siteVsCatClick[]
      number of clicks for a particular site for each different category
     siteVsCatImp[]
      number of displays for a particular site for each different category
     discount
      used to create variable frequency; discount finds average of
      all current category frequencies to which the site belongs
    FUNCTIONS:
     Site()
      The Constructor function for the Site class; initalizes all
      pertinent site variables after creating the particular site
      object 
     RestartSiteCount()
      Initializes all click and impression counter for a particular site    
*/

  double frequency;
  long intFreq;
  int numMatchingSites;
  double matchingBanners[]    = new double[(int) MiscFunc.NUM_SITES / 2];
  double matchingCategories[] = new double[(int) MiscFunc.NUM_SITES / 2];
  double siteVsCatBias[]      = new double[MiscFunc.NUM_CATEGORIES];
  double[] siteVsCatClick     = new double[MiscFunc.NUM_CATEGORIES];
  double[] siteVsCatImp       = new double[MiscFunc.NUM_CATEGORIES];
  double discount;

  /* Site class constructor -- initialize variables */
  public void RestartSiteCount() {
    int i;
    this.numDisplays      = 0.0;
    this.numClicks        = 0.0;
    this.numClicksTotal   = 0.0;
    this.numDisplaysTotal = 0.0;

    for (i=0; i < MiscFunc.NUM_CATEGORIES; i++) {
      siteVsCatClick[i] = 0.0;
      siteVsCatImp[i] = 0.0;
    }
  }

  public Site() {
    super();  // Calls superclass constructor (Agent)
    numMatchingSites = 0;
    intFreq = 0;
    int j;
    // initialize site bias value, then site vs. category bias values
    bias = miscFunctions.RandomVal(MiscFunc.MIN_SITE_BIAS,MiscFunc.MAX_SITE_BIAS);
    for (j=0; j < MiscFunc.NUM_CATEGORIES; j++) {
      siteVsCatBias[j] = miscFunctions.RandomVal(MiscFunc.MIN_SITE_VS_CAT_BIAS,
                         MiscFunc.MAX_SITE_VS_CAT_BIAS);
    }
    discount = 0.0;
  }
}
