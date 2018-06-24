package comparators;

import java.util.Comparator;

import models.CategoryModel;

public class sortMostFreq implements Comparator<CategoryModel> {
    @Override
    public int compare(CategoryModel categoryModel, CategoryModel t1) {
        return Integer.compare(t1.getDate(), categoryModel.getDate());
    }
}
