/* Agent.java
     
   Sasha Chislenko, Madan Ramakrishnan

   Updates:

06/08/98 - Sasha
     Fixed bias parameter ranges (was between MIN and MIN)
05/25/98 - Sasha
   - added numClicksTotal,numDisplaysTotal
   - changed parameters
*/

public class Agent extends HyperE {
/*
    VARIABLES:
      bias
       stores bias for an a Agent
      numCategoriesIn
       number of categories a particular agent is a member of
      numClicks
       for banners, the number of times a banner has been
       clicked in the last 100 accesses; for sites, the number
       of times a banner shown on a site has been clicked
       in the last 100 accesses
      numDisplays
       similar to numClicks, except number of displays (impresssions)
      numClicksTotal
       for banners, the number of times a banner has been
       clicked; for sites, the number of times a banner shown
       on a site has been clicked
      numDisplaysTotal
       similar to numClicksTotal, except number of displays (impresssions)
      biasPeriod,biasShift,biasAmplitude
       used in variable model; bias now fluctuates sinusoidally
       with period biasPeriod, amplitude biasAmplitude, and phase shift
       biasShift
      birthTime
       time of birth
      deathTime
       time of death
      categoresIn
       array which stores the ID #'s of all categories to which
       a particular agent belongs

    FUNCTIONS:
     Agent()
      Constructor function initializes all class variables
*/
  double bias;
  int numCategoriesIn;
  double numClicks,numDisplays,numClicksTotal,numDisplaysTotal;
  double biasPeriod,biasShift,biasAmplitude;
  int categoriesIn[] = new int[MiscFunc.MAX_CAT_PER_AGENT];
  MiscFunc miscFunctions;
  long birthTime,deathTime;

  public Agent() {
    int i,j;
    boolean badCategory;

    // initialize bias values and click/impression counters
    biasShift     = miscFunctions.RandomVal(MiscFunc.MIN_BIAS_SHIFT, MiscFunc.MAX_BIAS_SHIFT);
    biasPeriod    = miscFunctions.RandomVal(MiscFunc.MIN_BIAS_PERIOD,MiscFunc.MAX_BIAS_PERIOD);
    biasAmplitude = miscFunctions.RandomVal(MiscFunc.MIN_BIAS_AMP,   MiscFunc.MAX_BIAS_AMP);
    numDisplays      = 0.0;
    numClicks        = 0.0;
    numDisplaysTotal = 0.0;
    numClicksTotal   = 0.0;
    birthTime = 0;
    deathTime = 9999999;

    // determine # of categories an agent will be associated with;
    // then find that # of categories randomly and initialize
    // categoriesIn
    numCategoriesIn = miscFunctions.RandomVal(MiscFunc.MIN_CAT_PER_AGENT,MiscFunc.MAX_CAT_PER_AGENT);
    for (i = 0; i < numCategoriesIn; i++) {
      do {
        badCategory = false;
        categoriesIn[i] = miscFunctions.RandomVal(0,MiscFunc.NUM_CATEGORIES);
        for (j = 0; (j <= i - 1) && !badCategory; j++) {
          badCategory = (categoriesIn[i] == categoriesIn[j]);
        }
      } while (badCategory);
    }
  }
}
