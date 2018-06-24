package comparators;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import models.CategoryModel;
import models.ExpenditureModel;

public class sortLastModified implements Comparator<CategoryModel> {

    private List<ExpenditureModel> expenditureModelList;

    public sortLastModified(List<ExpenditureModel> expList){
        this.expenditureModelList = expList;
        sortExpList();
    }

    @Override
    public int compare(CategoryModel categoryModel, CategoryModel t1) {

        for(ExpenditureModel expModel : expenditureModelList){
            if(expModel.getRefID().equals(categoryModel.getName())){
                return -1;
            }else if(expModel.getRefID().equals(t1.getName())){
                return 1;
            }
        }
        return 0;
    }

    private void sortExpList(){
        Collections.sort(expenditureModelList, new expSortByDate());
    }
}