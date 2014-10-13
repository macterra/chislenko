/* Agent.java

   Sasha Chislenko, Madan Ramakrishnan

   Updates:

   05/25/98 - Sasha
   - added numClicksTotal,numDisplaysTotal
   - changed parameters
*/

public class Agent extends HyperE {
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

    biasShift = miscFunctions.RandomVal(MiscFunc.MIN_BIAS_SHIFT,MiscFunc.MAX_BIAS_SHIFT);
    biasPeriod = miscFunctions.RandomVal(MiscFunc.MIN_BIAS_PERIOD,MiscFunc.MIN_BIAS_PERIOD);
    biasAmplitude = miscFunctions.RandomVal(MiscFunc.MIN_BIAS_AMP,MiscFunc.MIN_BIAS_AMP);
    numDisplays = (double) 0.0;
    numClicks = (double) 0.0;
    numDisplaysTotal = (double) 0.0;
    numClicksTotal = (double) 0.0;
    birthTime = 0;
    deathTime = 9999999;
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
