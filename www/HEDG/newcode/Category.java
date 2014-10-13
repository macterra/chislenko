/* Category.java

 Sasha Chislenko, Madan Ramakrishnan

Modifications:

06/10/98 - Madan
        - changed formula in CalcCatFreq method
06/09/98 - Madan
        - added variable category frequency variables
        - added CalcCatFreq method
06/08/98 - Sasha
        - Renamed category fluct_bias parameters into biasXXX
06/08/98 - Madan
        - Class now represents data for one category, not for all
        - Eliminated all references to sitesPerCat[][NUM_SITES];
          numSitesPerCat now used exclusively
06/04/98 - Sasha
        - added category total click/display variables
06/02/98 - Madan
        - Added category vs. category click/display variables
05/27/98 - Sasha
        - Removed CAT_VS_CAT biases, were unused
05/25/98 - Sasha
        - Fixed fluct stuff, seems to need more testing.
*/

public class Category extends HyperE{
/*
    VARIABLES:
      biasAmplitude, biasPeriod, biasShift, bias
       used in obtaining a bias that fluctuates sinusoidally
       with time
      catClicks
       number of clicks for a category in the last 100
       accesses (used in variable model)
      catDisplays
       number of displays for a category in the last 100
       accesses (used in variable model)
      catClicksTotal
       total number of clicks for a category (that is, all 
       banners that are in a category)
      catDisplaysTotal
       total number of displays for a category (that is, all
       banners that are in a category)
      freqShift, freqAmplitude, freqPeriod
       variables used to obtain a sinusoidally changing
       category frequency
    FUNCTIONS:
     Category()
      Constructor function initializes all class variables
     RestartCatCount()
      Initializes all click and impression counters
     CalcCatFreq()
      Determines current frequency of a particular category
      (frequency changes sinusoidally with time)
*/
  double biasAmplitude,
         biasPeriod,
         biasShift,
         bias,
         catClicks,
         catDisplays,
         catClicksTotal,
         catDisplaysTotal,
         freqShift,
         freqAmplitude,
         freqPeriod;
  int[] sitesPerCat = new int[MiscFunc.NUM_SITES];
  int numSitesPerCat;

  public void RestartCatCount() {
    catClicks        = 0.0;
    catDisplays      = 0.0;
    catClicksTotal   = 0.0;
    catDisplaysTotal = 0.0;
  }

  public double CalcCatFreq(long currentTime) {
    return (1.0 + freqAmplitude * Math.sin((double) currentTime / this.freqPeriod +  freqShift));
  }

  public Category() {
    MiscFunc miscFunctions;
    miscFunctions = new MiscFunc();

    // initialize bias values
    bias          = miscFunctions.RandomVal(MiscFunc.MIN_CAT_BIAS,     MiscFunc.MAX_CAT_BIAS);
    biasAmplitude = miscFunctions.RandomVal(MiscFunc.MIN_CAT_AMPLITUDE,MiscFunc.MAX_CAT_AMPLITUDE);
    biasPeriod    = miscFunctions.RandomVal(MiscFunc.MIN_CAT_PERIOD,   MiscFunc.MAX_CAT_PERIOD);
    biasShift     = miscFunctions.RandomVal(MiscFunc.MIN_CAT_SHIFT,    MiscFunc.MAX_CAT_SHIFT);

    freqAmplitude = miscFunctions.RandomVal(MiscFunc.MIN_CAT_FREQ_AMP,   MiscFunc.MAX_CAT_FREQ_AMP);
    freqPeriod    = miscFunctions.RandomVal(MiscFunc.MIN_CAT_FREQ_PERIOD,MiscFunc.MAX_CAT_FREQ_PERIOD);
    freqShift     = miscFunctions.RandomVal(MiscFunc.MIN_CAT_FREQ_SHIFT, MiscFunc.MAX_CAT_FREQ_SHIFT);

    numSitesPerCat = 0;
  }
}
