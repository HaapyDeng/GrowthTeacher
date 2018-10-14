package com.mpl.GrowthTeacher.Tools;

import java.util.ArrayList;

public interface DelteableInterface {
    void choose(int position);

    void delete();

    int chooseCount();

    ArrayList<String> chooseId();
}
