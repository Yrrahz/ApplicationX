package comparators;
import java.util.Comparator;

import models.CategoryModel;

public class sortByAmount implements Comparator<CategoryModel> {
    @Override
    public int compare(CategoryModel categoryModel, CategoryModel t1) {
        return Integer.compare(t1.getTotalAmount(), categoryModel.getTotalAmount());
    }
}