/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ttm.cluster.main;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

/**
 *
 * @author palgunadi
 */
public class KMedoid {
    private ArrayList<Cluster> arrLCluster;
    private ArrayList<Cluster> arrLClusterLast;
    private ArrayList<Cluster> arrLClusterNew;
    private final ArrayList<Point2D> arrLPoint2D;
    private String[] getPointC;
    private double cost;
    private final int jmlCluster;
    private Point2D[] centroid;
    private final Point2D[] newCentroid;
    private boolean isPrintInfo;
    public final int MODE_ORDERED = 0;
    public final int MODE_RAND = 1;
    private int MODE;
    
    public KMedoid(ArrayList<Point2D> arrLPoint2D, int jmlCluster){
        this.arrLPoint2D = arrLPoint2D;
        this.jmlCluster = jmlCluster;
        this.centroid = new Point2D[jmlCluster];
        
        arrLCluster = new ArrayList<>();
        arrLClusterLast = new ArrayList<>();
        arrLClusterNew = new ArrayList<>();
        centroid = generateCentroid();
        newCentroid = new Point2D[jmlCluster];
        getPointC = new String[arrLPoint2D.size()];
        
        MODE = 0;
        isPrintInfo = false;
        
        arrLPoint2D.stream().map((p) -> new Cluster(p,"C0")).forEachOrdered((c) -> {
            arrLCluster.add(c);
        });
    }
    
    public void setMode(int value){
        this.MODE = value;
        centroid = generateCentroid();
    }
    
    public void setIsPrintInfo(boolean value){
        this.isPrintInfo = value;
    }
    
    public boolean isNotSameMemberInCluster() {
        if(arrLClusterLast.isEmpty()){
            return true;
        }else {
            for(int i=0; i < arrLCluster.size();i++){
                Cluster c1 = arrLCluster.get(i);
                Cluster c2 = arrLClusterLast.get(i);
                if(!c1.nameCluster.equals(c2.nameCluster)){
                    return true;
                }
            }
        }
        return false;
    }
    
    public String[] getPointC(){
        return getPointC;
    }
    
    public double getCost(){
        return cost;
    }
    
    public Point2D[] getCentroid(){
        return centroid;
    }
    
    public void proses(){
        double costFirst;
        
        sub_process_01();
        arrLClusterLast = (ArrayList<Cluster>) arrLCluster.clone();
        centroid = generateCentroid();
        costFirst = cost;
        //System.out.println("Nilai cost 1 ="+cost+" First cost ="+costFirst);
        
        sub_process_01();
        //System.out.println("Nilai cost 2 ="+cost+" First cost ="+costFirst);
        
        if(costFirst > cost){
            arrLClusterNew = (ArrayList<Cluster>) arrLCluster.clone();
            centroid = generateCentroid();
        }
    }
    
    private void sub_process_01(){
        
        cost = 0.0d;
        
        for(int i=0; i < arrLCluster.size(); i++){
            Cluster c1 = arrLCluster.get(i);
            Double dEuclidean = Double.MAX_VALUE;
            int iCluster = -1;
            for(int j=0; j < jmlCluster; j++){
                Point2D tempC = centroid[j];
                Double tempE = Math.sqrt(Math.pow(c1.p.getX()-tempC.getX(),2.0d)+
                        Math.pow(c1.p.getY()-tempC.getY(),2.0d));
//System.out.println(c1.p.getX()+","+c1.p.getY()+"--"+tempC.getX()+","+tempC.getY()+"="+tempE);                
                if(dEuclidean >= tempE){
                    iCluster = j;
                    dEuclidean = tempE;
                    
                }
            }
            
            cost = cost + Math.abs((c1.p.getX() - centroid[iCluster].getX()))+ 
                    Math.abs((c1.p.getY() - centroid[iCluster].getY()));
            Cluster newC = new Cluster(c1.p, String.valueOf(iCluster+1));
            getPointC[i]=c1.p.getX()+","+c1.p.getY()+" "+iCluster;
            arrLCluster.set(i, newC);         
        }
        
        if(isPrintInfo){
            printInfo();
        }
    }

    private Point2D[] generateCentroid() {
        if(MODE == MODE_ORDERED){
            return arrLPoint2D.subList(0, jmlCluster).toArray(centroid);
        }  
        if (MODE == MODE_RAND){
            ArrayList<Integer> list = new ArrayList<>();
            Point2D[] loCentroid = new Point2D[jmlCluster];            
            for(int i=0; i < arrLPoint2D.size(); i++){
                list.add(i);
            }
            Collections.shuffle(list);
            for(int j=0; j < jmlCluster; j++){
                int index = list.get(j);
                loCentroid[j] = arrLPoint2D.get(index);
            }
            return loCentroid;
        }
        return null;
    }
    
    private void printInfo() {
        System.out.println("Centroid yang lama :"+Arrays.toString(centroid));
        
        String temp = "";
        for(Cluster c1 : arrLCluster){
            temp = temp+c1.p.getX()+","+c1.p.getY()+" --> "+c1.nameCluster+"\n";
        }
        System.out.print(temp);
        System.out.println("Nilai Cost :"+cost);
        //System.out.println("Centroid yang baru :"+Arrays.toString(newCentroid));
    }
}
