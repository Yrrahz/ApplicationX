package comparators;

import java.util.Comparator;

import models.CategoryModel;

public class sortAlphabetically implements Comparator<CategoryModel> {
    @Override
    public int compare(CategoryModel categoryModel, CategoryModel t1) {
        return categoryModel.getName().compareTo(t1.getName());
    }
}
