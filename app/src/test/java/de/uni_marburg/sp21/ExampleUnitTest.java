package de.uni_marburg.sp21;

import android.os.Bundle;
import android.widget.TimePicker;

import org.junit.Test;

import java.util.List;

import de.uni_marburg.sp21.company_data_structure.Company;
import de.uni_marburg.sp21.filter.CheckItem;
import de.uni_marburg.sp21.filter.Filter;
import de.uni_marburg.sp21.filter.PickedTime;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {



        MainActivity a = new MainActivity();
        List<Company> companies = a.companies;
        assertEquals(a.companies.get(0), Filter.filter("B",a.companies, new CheckItem[]{new CheckItem("test")}, new CheckItem[]{new CheckItem("test")}, new CheckItem[]{new CheckItem("test")}, new CheckItem[]{new CheckItem("test")},false, false, new PickedTime()));
    }
}