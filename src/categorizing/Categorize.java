/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package categorizing;

import Utils.IOUtils;
import weka.core.converters.ConverterUtils.DataSource;
import weka.core.Instances;
import weka.core.Instance;
import weka.classifiers.trees.J48;
import weka.classifiers.trees.HoeffdingTree;
import weka.classifiers.bayes.NaiveBayesMultinomial;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.meta.ClassificationViaRegression;
import weka.classifiers.Evaluation;
import weka.classifiers.Classifier;
import weka.classifiers.AbstractClassifier;
import java.util.*;
import java.io.*;
import java.util.jar.*;
//import weka.classifiers.bayes.BayesianLogisticRegression;
//import weka.classifiers.misc.VFI;

/**
 *
 * @author Jeff
 */
public class Categorize {

    public static HashMap<String, String> classifierMap = new HashMap();
    public static final String BASE_CLASSIFIER = "weka.classifiers.AbstractClassifier";
    public static final String BASE_PATH = "weka.";
    public static ArrayList<String> outList = new ArrayList();
    public static long[] seedSet = new long[10];

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Random rand = new Random(System.currentTimeMillis());
        for (int i1 = 0; i1 < seedSet.length; i1++) {
            for(int i2=0;i2<rand.nextInt(50);i2++){
                rand.nextInt();
            }
            seedSet[i1] = rand.nextLong();
        }
        String fName = "testFile.ARFF";
        int minAttributes = 3;
        int maxAttributes = 3;
        int[][][] attSets = getAttributeSets(minAttributes, maxAttributes);
        GenerateTestData2 tester = null;
        populateClassifierMap();
        ArrayList<String> kList = new ArrayList();
        for (String tmpKey : classifierMap.keySet()) {
            System.out.println(tmpKey);
            kList.add(tmpKey);
        }
        for (int i1 = 0; i1 < attSets.length; i1++) {
            runTheClassifiers(fName, kList, attSets[i1], i1, minAttributes + i1);
        }

    }

    public static void runTheClassifiers(String fName, ArrayList<String> kList) {
        GenerateTestData2 tester = new GenerateTestData2(fName);
        tester.setDatasetName("test 1");
        tester.setNumAttributes(6);
        tester.setNumBinary(5);
        tester.setNumDiscrete(1);
        tester.setNumReal(0);
        tester.setNumCategories(4);
        tester.setNumDataPoints(500);
        tester.numTestPoints = 1000;
        tester.generate();
        DataSource source = null;
        Instances instances = null;
        Evaluation eval = null;
//        Evaluation eval2 = null;
//        Evaluation eval3 = null;
        for (String tmpKey : kList) {
            System.out.println("\n\n" + tmpKey + "\n");
            Classifier tmpC = getClassifier(tmpKey);
            if (tmpC != null) {
                for (int iSeed = 0; iSeed < seedSet.length; iSeed++) {

                    try {
                        source = new DataSource(fName);
                        instances = source.getDataSet();
                        instances.setClassIndex(instances.numAttributes() - 1);
                        tmpC.buildClassifier(instances);
//                        DataSource testSource = new DataSource(fName.replace(".ARFF", "-test.ARFF"));
//                        Instances inst2 = testSource.getDataSet();
//                        inst2.setClassIndex(inst2.numAttributes() - 1);
//                        for (int i1 = 0; i1 < inst2.numInstances(); i1++) {
//                            Instance tmpI = inst2.get(i1);
//                            double testVal = tmpC.classifyInstance(tmpI);
//                            double scr = tester.oList.get(i1).score / tester.maxScore * tester.numCategories;
//                        }
                        eval = new Evaluation(instances);
                        eval.crossValidateModel(tmpC, instances, 10, new java.util.Random(seedSet[iSeed]));

                    } catch (Exception ex) {

                    }
                    System.out.println(eval.toSummaryString());
//                eval.
                }
            }
        }
    }

    public static void runTheClassifiers(String fName, ArrayList<String> kList, int[][] aSets, int i1, int minAtts) {

        String tmpS = "";
        for (int i2 = 0; i2 < aSets.length; i2++) {
            GenerateTestData2 tester = new GenerateTestData2(fName);
            tester.setDatasetName("test " + i1 + "-" + i2);
            tester.setNumAttributes(minAtts + i1);
            tester.setNumBinary(aSets[i2][0]);
            tester.setNumDiscrete(aSets[i2][1]);
            tester.setNumReal(aSets[i2][2]);
            tester.setNumCategories(4);
            tester.setNumDataPoints(500);
            tester.numTestPoints = 1000;
            tester.generate();
            DataSource source = null;
            Instances instances = null;
            Evaluation eval = null;
            double sumGood = 0.;
            for (String tmpKey : kList) {
                System.out.println("\n\n" + tmpKey + "\n");
                Classifier tmpC = getClassifier(tmpKey);
                if (tmpC != null) {
                        sumGood = 0.;
                    for (int iSeed = 0; iSeed < seedSet.length; iSeed++) {
                        try {
                            tmpS = "test " + i1 + "-" + i2;
                            tmpS += "," + (minAtts + 1);
                            tmpS += "," + aSets[i2][0] + "," + aSets[i2][1] + "," + aSets[i2][2];
                            tmpS += "," + tmpKey;
                            source = new DataSource(fName);
                            instances = source.getDataSet();
                            instances.setClassIndex(instances.numAttributes() - 1);
                            tmpC.buildClassifier(instances);
//                            DataSource testSource = new DataSource(fName.replace(".ARFF", "-test.ARFF"));
//                            Instances inst2 = testSource.getDataSet();
//                            inst2.setClassIndex(inst2.numAttributes() - 1);
//                            for (int i3 = 0; i3 < inst2.numInstances(); i3++) {
//                                Instance tmpI = inst2.get(i3);
//                                double testVal = tmpC.classifyInstance(tmpI);
//                                double scr = tester.oList.get(i3).score / tester.maxScore * tester.numCategories;
//                            }
                            eval = new Evaluation(instances);
                            eval.crossValidateModel(tmpC, instances, 10, new java.util.Random(seedSet[iSeed]));

                        } catch (Exception ex) {

                        }
                        sumGood += eval.correct();
                        System.out.println(eval.toSummaryString());
//                        tmpS += "," + eval.correct();
                    }
                    tmpS += "," + (sumGood / ((double) (tester.numDataPoints * seedSet.length)));
                    outList.add(tmpS);
                }
            }
        }
        IOUtils.arrayList2file(new File("outList.csv"), outList);
    }

    public static int[][][] getAttributeSets(int min, int max) {
        int numSets = max - min + 1;
        int[][][] sets = new int[numSets][][];
        for (int i1 = min; i1 <= max; i1++) {
            int counter = 0;
            int num = ((i1 + 2) * (i1 + 1)) / 2;
            sets[i1 - min] = new int[num][3];
            for (int numBinary = 0; numBinary <= i1; numBinary++) {
                for (int numDiscrete = 0; numDiscrete < i1 - numBinary + 1; numDiscrete++) {
//                    for (int numReal = i1-numBinary-numDiscrete; numReal < i1 - numBinary - numDiscrete; numReal++) {
                    sets[i1 - min][counter][0] = numBinary;
                    sets[i1 - min][counter][1] = numDiscrete;
                    sets[i1 - min][counter++][2] = i1 - numDiscrete - numBinary;
//                    }
                }
            }
        }
        return sets;
    }

    public static AbstractClassifier getAbstractClassifier(String anType) {
        populateClassifierMap();
        if (classifierMap.keySet().contains(anType)) {
            try {
                AbstractClassifier tmpC = (AbstractClassifier) Class.forName(classifierMap.get(anType)).newInstance();
                return tmpC;
            } catch (Exception ex) {
            }
        }
        return null;
    }

    public static Classifier getClassifier(String anType) {
        populateClassifierMap();
        if (classifierMap.keySet().contains(anType)) {
            try {
                Classifier tmpC = (Classifier) Class.forName(classifierMap.get(anType)).newInstance();
                return tmpC;
            } catch (Exception ex) {
            }
        }
        return null;
    }

    public static void populateClassifierMap() {
        classifierMap.clear();
        Set<Class<?>> classSet = JarScanner.getFromJARFile("dist/lib/weka.jar", "weka.classifiers");
        for (Iterator<Class<?>> it = classSet.iterator(); it.hasNext();) {
            Class tmpC = it.next();
            Class tmpSC = tmpC.getSuperclass();
            if (tmpSC != null) {
                String tmpS = tmpSC.toString().replace("class ", "");
                if (tmpS.matches(BASE_CLASSIFIER)) {
                    String name = tmpC.getName();
                    String shortName = name.replace(BASE_PATH, "");
                    classifierMap.put(shortName, name);
                }
            }
        }

    }

}
