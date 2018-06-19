package comparators;

import java.util.Comparator;

import booken.yrrah.applicationx.CategoryModel;

public class sortAlphabetically implements Comparator<CategoryModel> {
    @Override
    public int compare(CategoryModel categoryModel, CategoryModel t1) {
        return categoryModel.getName().compareTo(t1.getName());
    }
}
