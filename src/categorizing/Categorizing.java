/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package categorizing;

import weka.core.converters.ConverterUtils.DataSource;
import weka.core.Instances;
import weka.core.Instance;
import weka.classifiers.trees.J48;
import weka.classifiers.trees.HoeffdingTree;
import weka.classifiers.bayes.NaiveBayesMultinomial;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.meta.ClassificationViaRegression;
import weka.classifiers.Evaluation;
//import weka.classifiers.bayes.BayesianLogisticRegression;
//import weka.classifiers.misc.VFI;

/**
 *
 * @author Jeff
 */
public class Categorizing {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        String fName = "testFile.ARFF";
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
        Evaluation eval=null;
        Evaluation eval2=null;
        Evaluation eval3=null;
        int numJ48miss = 0;
        int numHmiss = 0;
        int numNBmiss = 0;
        int numCVRmiss = 0;
        try {
            source = new DataSource(fName);
            instances = source.getDataSet();
            instances.setClassIndex(instances.numAttributes() - 1);
            System.out.println("\nDataset:\n");
            System.out.println(instances);
            J48 j = new J48();
            j.buildClassifier(instances);
            HoeffdingTree h = new HoeffdingTree();
            h.buildClassifier(instances);
            NaiveBayes nb = new NaiveBayes();
            nb.buildClassifier(instances);
            ClassificationViaRegression cvr = new ClassificationViaRegression();
            cvr.buildClassifier(instances);
            DataSource testSource = new DataSource(fName.replace(".ARFF", "-test.ARFF"));
            Instances inst2 = testSource.getDataSet();
            inst2.setClassIndex(inst2.numAttributes() - 1);
            for (int i1 = 0; i1 < inst2.numInstances(); i1++) {
                Instance tmpI = inst2.get(i1);
                String rslts = " OK";
                double testVal = j.classifyInstance(tmpI);
                double scr = tester.oList.get(i1).score/tester.maxScore*tester.numCategories;
                    if (Math.abs(testVal - tmpI.classValue()) > 1.e-2) {
                    rslts = "  MISSED !!!! "+scr;
                    numJ48miss++;
                }
                System.out.println("instance " + i1 + " calssified by J48 as " + testVal + " with actual " + tmpI.classValue() + rslts);
                rslts = " OK";
                testVal = h.classifyInstance(tmpI);
                if (Math.abs(testVal - tmpI.classValue()) > 1.e-2) {
                    rslts = "  MISSED !!!! "+scr;
                    numHmiss++;
                }
                System.out.println("instance " + i1 + " calssified by HoeffdingTree as " + testVal + " with actual " + tmpI.classValue() + rslts);
                rslts = " OK";
                testVal = nb.classifyInstance(tmpI);
                if (Math.abs(testVal - tmpI.classValue()) > 1.e-2) {
                    rslts = "  MISSED !!!! "+scr;
                    numNBmiss++;
                }
                System.out.println("instance " + i1 + " calssified by NaiveBayes as " + testVal + " with actual " + tmpI.classValue() + rslts);
                rslts = " OK";
                testVal = cvr.classifyInstance(tmpI);
                if (Math.abs(testVal - tmpI.classValue()) > 1.e-2) {
                    rslts = "  MISSED !!!! "+scr;
                    numCVRmiss++;
                }
                System.out.println("instance " + i1 + " calssified by CVR as " + testVal + " with actual " + tmpI.classValue() + rslts);
            }
        eval = new Evaluation(instances);
        eval2 = new Evaluation(instances);
        eval3 = new Evaluation(instances);
        J48 tree = new J48();
        nb=new NaiveBayes();
        cvr = new ClassificationViaRegression();
        eval.crossValidateModel(tree, instances, 10, new java.util.Random(1));
        eval2.crossValidateModel(nb, instances, 10, new java.util.Random(1));
        eval3.crossValidateModel(cvr, instances, 10, new java.util.Random(1));
        
        } catch (Exception ex) {

        }
        System.out.println("\n\nNum miss by J48 = " + numJ48miss );
        System.out.println("Num miss by H = " + numHmiss );
        System.out.println("Num miss by NB = " + numNBmiss);
        System.out.println("Num miss by CVR = " + numCVRmiss+"\n\n");
        System.out.println(eval.toSummaryString());
        System.out.println(eval2.toSummaryString());
        System.out.println(eval3.toSummaryString());
        
    }

}
