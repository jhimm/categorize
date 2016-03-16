/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package categorizing;

/**
 *
 * @author Jeff
 */
public class Observation {
    public int[] indices=null;
    public double score=0.;
    public String oString = "";
    public Observation(int num){
        this.indices=new int[num];
    }
    public void setScore(double s){
        this.score=s;
    }
    public double getScore(){
        return this.score;
    }
    public void setIndexValue(int i,int val){
        this.indices[i]=val;
    }
    public int getindexVal(int i){
        return this.indices[i];
    }
    public int[] getIndices(){
        return this.indices;
    }
    public void setOString(String o){
        this.oString=o;
    }
    public String getOString(){
        return this.oString;
    }
}
