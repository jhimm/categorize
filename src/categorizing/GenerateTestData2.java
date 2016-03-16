/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package categorizing;

import java.io.File;
import java.util.ArrayList;
import Utils.*;
import java.util.Random;

/**
 *
 * @author Jeff
 */
public class GenerateTestData2 {

    public String outFileName = "testData.csv";
    public String datasetName = "name";
    public int numBinary = 0;
    public int numDiscrete = 0;
    public int numReal = 1;
    public int numDataPoints = 100;
    public int numTestPoints = 100;
    public int trainingSetSize = 20;
    public int numCategories = 5;
    public int numAttributes = 8;
    public double wtSum=0.;
    public double maxScore=0.;
    public Random random = null;
    public ArrayList<Double> wList = new ArrayList();
    public ArrayList<String> sList = new ArrayList();
    public ArrayList<String> tList = new ArrayList();
    public ArrayList<Double> rList = new ArrayList();
    public ArrayList<Observation> oList = new ArrayList();

    public GenerateTestData2(String inf) {
        this.outFileName = inf;
        random = new Random(System.currentTimeMillis());
    }

    public void setNumDataPoints(int ndp) {
        this.numDataPoints = ndp;
//        for (int i1 = 0; i1 < ndp; i1++) {
//            wList.add(2.*random.nextDouble());
//        }
    }

    public void setNumBinary(int nb) {
        this.numBinary = nb;
    }

    public void setNumAttributes(int na) {
        this.numAttributes = na;
        for (int i1 = 0; i1 < na; i1++) {
            wList.add(random.nextDouble());
            wtSum+=wList.get(i1);
        }
    }

    public void setNumCategories(int nc) {
        this.numCategories = nc;
    }

    public void setNumDiscrete(int nd) {
        this.numDiscrete = nd;
    }

    public void setNumReal(int nr) {
        this.numReal = nr;
    }

    public void setDatasetName(String nm) {
        this.datasetName = nm;
    }

    public void generate() {
        sList.add("%Sample for training classifier");
        sList.add("%some additional comments ");
        sList.add("@relation training_for_weka");
        tList.add("%Sample for testing classifier");
        tList.add("%some additional comments ");
        tList.add("@relation test_for_weka");
        String tmpS;
//        double maxScore = (double) (3 * numDiscrete + numReal + numBinary);
//        maxScore/=wtSum;
        for (int i1 = 0; i1 < numBinary; i1++) {
            tmpS = "@attribute b" + i1 + " {yes no}";
            sList.add(tmpS);
            tList.add(tmpS);
        }
        for (int i1 = 0; i1 < numDiscrete; i1++) {
            tmpS = "@attribute d" + i1 + " {0 1 2 3}";
            sList.add(tmpS);
            tList.add(tmpS);
        }
        for (int i1 = 0; i1 < numReal; i1++) {
            tmpS = "@attribute r" + i1 + " real";
            sList.add(tmpS);
            tList.add(tmpS);
        }
        tmpS = "@attribute score ";
        if (numCategories == 0) {
            tmpS += "real";
        } else {
            tmpS += "{";
            for (int i1 = 0; i1 < numCategories; i1++) {
                tmpS += " " + i1;
            }
            tmpS += "}";
        }
        sList.add(tmpS);
        tList.add(tmpS);
        sList.add("@data");
        sList.addAll(getData(numDataPoints));
        tList.add("@data");
        tList.addAll(getData(numTestPoints));
//        for (int i2 = 0; i2 < numDataPoints; i2++) {
//            tmpS = "";
//            double score = 0.;
//            int counter = 0;
//            for (int i1 = 0; i1 < numBinary; i1++) {
//                boolean val = random.nextBoolean();
//                tmpS += (val ? "yes," : "no,");
//                score += wList.get(counter++) * (val ? 1 : 0);
//            }
//            for (int i1 = 0; i1 < numDiscrete; i1++) {
//                int val = random.nextInt(4);
//                tmpS += val + ",";
//                score += wList.get(counter++) * (val);
//            }
//            for (int i1 = 0; i1 < numReal; i1++) {
//                double val = random.nextDouble();
//                tmpS += val + ",";
//                score += wList.get(counter++) * (val);
//            }
//            tmpS += (int) (numCategories * (score / maxScore));
//            sList.add(tmpS);
//        }
        IOUtils.arrayList2file(new File(this.outFileName), sList);
        IOUtils.arrayList2file(new File(this.outFileName.replace(".ARFF", "-test.ARFF")), tList);
        tmpS = "weights, ";
        for (int i1 = 0; i1 < this.numAttributes; i1++) {
            tmpS += wList.get(i1) + ", ";
        }
        File tmpF = new File(this.outFileName.replace(".ARFF", "-weights.csv"));
        IOUtils.string2file(tmpF, tmpS, Boolean.TRUE);

    }

    public ArrayList<String> getData(int num ) {
        maxScore=0;
        oList.clear();
        ArrayList<String> tmpList = new ArrayList();
        String tmpS;
        int realCount=0;
        for (int i2 = 0; i2 < num; i2++) {
            tmpS = "";
            Observation obs=new Observation(this.numAttributes);
            double score = 0.;
            int counter = 0;
            for (int i1 = 0; i1 < numBinary; i1++) {
                boolean val = random.nextBoolean();
                tmpS += (val ? "yes," : "no,");
                obs.setIndexValue(counter, (val ? 1 : 0));
                score += wList.get(counter++) * (val ? 1 : 0);
            }
            for (int i1 = 0; i1 < numDiscrete; i1++) {
                int val = random.nextInt(4);
                tmpS += val + ",";
                obs.setIndexValue(counter, val );
                score += wList.get(counter++) * (val);
            }
            for (int i1 = 0; i1 < numReal; i1++) {
                double val = random.nextDouble();
                rList.add(val);
                tmpS += val + ",";
                obs.setIndexValue(counter, realCount++ );
                score += wList.get(counter++) * (val);
            }
            obs.setOString(tmpS);
            obs.setScore(score);
            oList.add(obs);
            if(score>maxScore)maxScore=score;
        }
        maxScore*=1.01;
        for(int i1=0;i1<num;i1++){
            Observation tmpO=oList.get(i1);
            tmpS=tmpO.getOString();
            if (numCategories == 0) {
                tmpS += tmpO.getScore();
            } else {
                int iScore = (int) (numCategories * (tmpO.getScore() / maxScore));
                if(iScore>=numCategories)iScore--;
                tmpS += iScore;
            }
            tmpList.add(tmpS);
        }
        
        return tmpList;
    }
}
