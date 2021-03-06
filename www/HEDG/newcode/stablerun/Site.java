/* Site.java
   Sasha Chislenko, Madan Ramakrishnan

   06/01/98 - Madan
     - added RestartSiteCount method
*/


public class Site extends Agent {
/* The site class is used to represent site objects.  It is a
   subclass of Agent
    CONSTANTS:
     NUM_SITES holds the total number of sites in the
      siteList array
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
     matchingSites
      a 2 dim. array that holds the ID# of every banner that can
      be shown on a particular site and the "sort criteria" of
      each of those sites (the sort criteria varies with the
      specific mode of scheduling)

    FUNCTIONS:
     Site()
      The Constructor function for the Site class; initalizes all
      pertinent site variables after creating the particular site
      object */

  double frequency;
  long intFreq;
  int numMatchingSites;
  double matchingSites[][] = new double[(int) MiscFunc.NUM_SITES / 2][3];
  double siteVsCatBias[]   = new double[MiscFunc.NUM_CATEGORIES];
  double[] siteVsCatClick  = new double[MiscFunc.NUM_CATEGORIES];
  double[] siteVsCatImp    = new double[MiscFunc.NUM_CATEGORIES];

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
    bias = miscFunctions.RandomVal(MiscFunc.MIN_SITE_BIAS,MiscFunc.MAX_SITE_BIAS);
    for (j=0; j < MiscFunc.NUM_CATEGORIES; j++) {
      siteVsCatBias[j] = miscFunctions.RandomVal(MiscFunc.MIN_SITE_VS_CAT_BIAS,
                         MiscFunc.MAX_SITE_VS_CAT_BIAS);
    }
  }
}
