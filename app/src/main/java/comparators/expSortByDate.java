package comparators;

import java.util.Comparator;

import models.ExpenditureModel;

public class expSortByDate implements Comparator<ExpenditureModel> {
    @Override
    public int compare(ExpenditureModel expModel, ExpenditureModel t1) {
        return Long.compare(t1.getDate(), expModel.getDate());
    }
}