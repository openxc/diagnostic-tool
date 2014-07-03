package com.openxc.openxcdiagnostic.diagnostic.saver;

import java.util.ArrayList;

import com.openxc.openxcdiagnostic.diagnostic.pair.Pair;

public interface Saver {

    public void removeAll();
    
    public ArrayList<Pair> getPairs();
    
}
