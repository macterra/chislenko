/* Category.java

 Sasha Chislenko, Madan Ramakrishnan

   06/02/98 - Madan
            - Added category vs. category click/display variables
   05/27/98 - Sasha
            - Removed CAT_VS_CAT biases, were unused
   05/25/98 - Sasha
            - Fixed fluct stuff, seems to need more testing.

   The class should eventually represent data for one category, not for all
*/

public class Category extends HyperE{
  double[] fluct_bias_amplitude = new double[MiscFunc.NUM_CATEGORIES];
  double[] fluct_bias_period    = new double[MiscFunc.NUM_CATEGORIES];
  double[] fluct_bias_shift     = new double[MiscFunc.NUM_CATEGORIES];
  double bias[] = new double[MiscFunc.NUM_CATEGORIES];
  double[] catClicks = new double[MiscFunc.NUM_CATEGORIES];
  double[] catDisplays = new double[MiscFunc.NUM_CATEGORIES];
  int[][] sitesPerCat = new int[MiscFunc.NUM_CATEGORIES][MiscFunc.NUM_SITES + 1];
  int[] numSitesPerCat = new int[MiscFunc.NUM_CATEGORIES];
    /* sitesPerCat[particularCategory][NUM_SITES] = total number of
       sites in particular category */

  public void RestartCatCount() {
    int i;
    for (i=0; i < MiscFunc.NUM_CATEGORIES; i++) {
      catClicks[i] = 0.0;
      catDisplays[i] = 0.0;
    }
  }

  public Category(Site[] siteList) {
    int i,j;
    MiscFunc miscFunctions;
    miscFunctions = new MiscFunc();

    for (i=0; i < MiscFunc.NUM_CATEGORIES; i++) {
      // eventually delete next line
      sitesPerCat[i][MiscFunc.NUM_SITES] = 0;

      fluct_bias_amplitude[i] = miscFunctions.RandomVal(MiscFunc.MIN_CAT_AMPLITUDE,MiscFunc.MAX_CAT_AMPLITUDE);
      fluct_bias_period[i]    = miscFunctions.RandomVal(MiscFunc.MIN_CAT_PERIOD,MiscFunc.MAX_CAT_PERIOD);
      fluct_bias_shift[i]     = miscFunctions.RandomVal(MiscFunc.MIN_CAT_SHIFT,MiscFunc.MAX_CAT_SHIFT);

      numSitesPerCat[i] = 0;

    }

    for (i=0; i < MiscFunc.NUM_SITES; i++) {
      for (j=0; j < siteList[i].numCategoriesIn; j++) {
        sitesPerCat[siteList[i].categoriesIn[j]][sitesPerCat[siteList[i].categoriesIn[j]][MiscFunc.NUM_SITES]]= i;
        // eventually delete next line
        sitesPerCat[siteList[i].categoriesIn[j]][MiscFunc.NUM_SITES]++;
        numSitesPerCat[siteList[i].categoriesIn[j]]++;
      }
    }

    for (i = 0; i < bias.length; i++) {
      bias[i] = miscFunctions.RandomVal(MiscFunc.MIN_CAT_BIAS,MiscFunc.MAX_CAT_BIAS);
    }
  }
}
