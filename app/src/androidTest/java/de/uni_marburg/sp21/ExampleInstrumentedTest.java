package de.uni_marburg.sp21;

import android.content.Context;

import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.platform.app.InstrumentationRegistry;

import com.google.firebase.firestore.FirebaseFirestore;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.uni_marburg.sp21.company_data_structure.Address;
import de.uni_marburg.sp21.company_data_structure.Category;
import de.uni_marburg.sp21.company_data_structure.Company;
import de.uni_marburg.sp21.company_data_structure.Message;
import de.uni_marburg.sp21.company_data_structure.Organization;
import de.uni_marburg.sp21.company_data_structure.ProductGroup;
import de.uni_marburg.sp21.filter.CheckItem;
import de.uni_marburg.sp21.filter.Filter;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.junit.Assert.assertEquals;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(MockitoJUnitRunner.class)
public class ExampleInstrumentedTest{
    List<Company> companies = new ArrayList<>();
    CheckItem[] type;
    CheckItem[] organizations;
    CheckItem[] restrictions;
     CheckItem[] categories;

    @Mock
    Context mMockContext;


    @Rule
    public ActivityScenarioRule<MainActivity> activityRule =
            new ActivityScenarioRule<>(MainActivity.class);


    @Before
    public void setUp(){
        Address address1 = new Address("Marburg", "Auf der Höhe 2A","35041");
        Address address2 = new Address("Gladenbach", "Freiherr-vom-Stein-Straße 28","35075");
        Address address3 = new Address("Battenberg", "Dorfstrsße 1","35088");
        DataBaseManager dataBaseManager = new DataBaseManager();
        FirebaseFirestore database = FirebaseFirestore.getInstance();

        // List<Company> companies = DataBaseManager.getCompanyList(database,a.getContext());
         Message message1 = new Message("24-02-2020", "Its good" );
        Message message2 = new Message("25-03-2020", "Its fine" );
        Message message3 = new Message("26-04-2020", "Its bad" );
        List<Message> messageList = new ArrayList<>();
        messageList.add(message1);
        messageList.add(message3);
        mMockContext.getResources().getString(R.string.address);

        Map<String, ArrayList<Map<String, String>>> openingHours1 = new HashMap<>();
        ArrayList<Map<String,String>> opened = new ArrayList<>();
        Map<String, String> hours = new HashMap<>();
        Map<String, String> hours2 = new HashMap<>();
        hours.put("start", "10:00");
        hours.put("end", "14:00");
        hours2.put("start", "15:00");
        hours2.put("end", "20:00");
        opened.add(hours);
        opened.add(hours2);

        openingHours1.put("monday", opened);
        openingHours1.put("tuesday", opened);
        openingHours1.put("wednesday", opened);
        openingHours1.put("thursday", opened);
        openingHours1.put("friday", opened);
        openingHours1.put("saturday", opened);
        openingHours1.put("sunday", null);

        Company company1 =  new Company("1");
        Company company2 =  new Company("2");
        Company company3 =  new Company("3");
        company1.setDeliveryService(false);
        company1.setAddress(address1);
        company1.setMessages(messageList);
        company1.setMail("lenny.daum@web.de");
        company1.setDescription("Something to test with");
        company1.setName("Rosenbauer");
        company1.setOwner("Baronin Lisann");
        company1.setOpeningHours(openingHours1);

        List<String> types = new ArrayList<>();
        types.add("Hotel");
        types.add("Restaurant");
        company1.setTypes(types);
        List<Organization> organizationList = new ArrayList<>();
        organizationList.add(new Organization(1.0, "Blue Organization", "https://exampleURL.com"));
        company1.setOrganizations(organizationList);
        List<ProductGroup> productGroups = new ArrayList<>();
        ProductGroup productGroup = new ProductGroup(Category.MEAT, 1.0);
        productGroup.setRawProd(false);

        URL a = ClassLoader.getSystemResource("string");
//        a.get
        company1.setProductGroups(productGroups);
        companies.add(company1);
         type = new CheckItem[]{ new CheckItem("Producer"), new CheckItem("Shop"), new CheckItem("restaurant"), new CheckItem("Hotel"), new CheckItem("Mart")};
         organizations = new CheckItem[]{new CheckItem("Blue Organization")};
         restrictions =  new CheckItem[]{new CheckItem("Company name"), new CheckItem("owner Name"), new CheckItem(" shop types"),
                 new CheckItem("Address"), new CheckItem("Company Description"), new CheckItem("product description")};
         categories = new CheckItem[]{new CheckItem(Category.MEAT.toString())};
    }

    @Test
    public void listGoesOverTheFold() {

        // FilterButton and BottomSheetDialog
        onView(withId(R.id.filterButton)).perform(click());
        onView(withId(R.id.linearLayout)).check(ViewAssertions.matches(isDisplayed()));

        onView(withId(R.id.categoryRV)).perform(click());

        List<Company> rosen = Filter.filter("Rosen", companies, type, organizations, categories, restrictions, false, false, InstrumentationRegistry.getInstrumentation().getTargetContext(), null, null, null);
        assertEquals(companies,rosen);

        List<Company> isOpn = Filter.filter("", companies, type, organizations, categories, restrictions, false, true, InstrumentationRegistry.getInstrumentation().getTargetContext(), null, null, null);
        assertEquals(companies, isOpn);

        List<Company> isDelivery = Filter.filter("", companies, type, organizations, categories, restrictions, true, false, InstrumentationRegistry.getInstrumentation().getTargetContext(), null, null, null);
        List<Company> empty = new ArrayList<>();
        assertEquals(empty, isDelivery);

        categories[0].check(true);
        List<Company> categoryFilter = Filter.filter("", companies, type, organizations, categories, restrictions, false, false, InstrumentationRegistry.getInstrumentation().getTargetContext(), null, null, null);
        //assertEquals(companies, categoryFilter);
        categories[0].check(false);

        type[2].check(true);
        List<Company> typeFilter = Filter.filter("", companies, type, organizations, categories, restrictions, false, false, InstrumentationRegistry.getInstrumentation().getTargetContext(), null, null, null);
        //assertEquals(companies, typeFilter);
        type[2].check(false);

        organizations[0].check(true);
        List<Company> organizationFilter = Filter.filter("", companies, type, organizations, categories, restrictions, false, false, InstrumentationRegistry.getInstrumentation().getTargetContext(), null, null, null);
        assertEquals(companies, organizationFilter);
        organizations[0].check(false);

        restrictions[1].check(true);
        List<Company> restrictionFilter = Filter.filter("Baronin", companies, type, organizations, categories, restrictions, false, false, InstrumentationRegistry.getInstrumentation().getTargetContext(), null, null, null);
        assertEquals(companies, restrictionFilter);


    }
    @Test
    public void useAppContext() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        assertEquals("de.uni_marburg.sp21", appContext.getPackageName());
    }
}