package njoize.dai_ka.com.demotestprint;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class FoodFragment extends Fragment {

    //    Explicit
    private String amountCustomerString;
    private boolean totalBillABoolean;
    private String idCategoryClick;
    private MyManageSQLite myManageSQLite;

    public FoodFragment() {
        // Required empty public constructor
    }

    public static FoodFragment foodInstante(String amountCustomer, boolean totalBill) {

        FoodFragment foodFragment = new FoodFragment();
        Bundle bundle = new Bundle();
        bundle.putString("Amount", amountCustomer);
        bundle.putBoolean("Bill", totalBill);
        foodFragment.setArguments(bundle);
        return foodFragment;

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        myManageSQLite = new MyManageSQLite(getActivity());

//        Receive Value
        receiveValue();

//        Category RecyclerView
        categoryRecyclerView();

//        Check Order
        checkOrder();

    }   // Main Method

    private void checkOrder() {

        try {

            SQLiteDatabase sqLiteDatabase = getActivity().openOrCreateDatabase(MyOpenHelper.database_name, Context.MODE_PRIVATE, null);
            Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM orderTABLE", null);
            cursor.moveToFirst();

            Log.d("25decV2", "Cursor Work");

            ArrayList<String> nameFoodStringArrayList = new ArrayList<>();
            ArrayList<String> amounStringArrayList = new ArrayList<>();
            ArrayList<String> priceSumStringArrayList = new ArrayList<>();
            final ArrayList<String> idSQLiteStringArrayList = new ArrayList<>();

            for (int i = 0; i < cursor.getCount(); i += 1) {

                nameFoodStringArrayList.add(cursor.getString(2));
                amounStringArrayList.add(cursor.getString(4));
                idSQLiteStringArrayList.add(cursor.getString(0));

                int priceInt = Integer.parseInt(cursor.getString(3));
                int amountInt = Integer.parseInt(amounStringArrayList.get(i));

                priceSumStringArrayList.add(Integer.toString(priceInt * amountInt));

                cursor.moveToNext();
            }   // for

            cursor.close();

            Log.d("25decV2", "nameFood ==> " + nameFoodStringArrayList.toString());

            RecyclerView recyclerView = getView().findViewById(R.id.recyclerOrder);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(),
                    LinearLayoutManager.VERTICAL, false);
            recyclerView.setLayoutManager(linearLayoutManager);

            OrderAdapter orderAdapter = new OrderAdapter(getActivity(), nameFoodStringArrayList, amounStringArrayList,
                    priceSumStringArrayList, new OnClickItem() {
                @Override
                public void onClickItem(View view, int positions) {
                    increaseOrDecrease(idSQLiteStringArrayList.get(positions));
                }
            });

            recyclerView.setAdapter(orderAdapter);



        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void increaseOrDecrease(final String idSQLite) {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setTitle("Increase or Decrease").setMessage("Please Click Button").setPositiveButton("Increase", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                toolIncDec(idSQLite, true);
            }
        }).setNegativeButton("Decrease", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                toolIncDec(idSQLite, false);
            }
        }).show();

    }

    private void toolIncDec(String idSQLite, boolean status) {

        SQLiteDatabase sqLiteDatabase = getActivity().openOrCreateDatabase(MyOpenHelper.database_name, Context.MODE_PRIVATE, null);
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM orderTABLE WHERE id = " + "'" + idSQLite + "'", null);
        cursor.moveToFirst();

        String amountString = cursor.getString(4);
        Log.d("25decV4", "Current Amount ==> " + amountString);

        int amountInt = Integer.parseInt(amountString);
        cursor.close();

        if (status) {
//            Increase Status
            amountInt += 1;
            sqLiteDatabase.execSQL("UPDATE orderTABLE SET Amount=" + "'" + Integer.toString(amountInt) + "'" + " WHERE id=" + "'" + idSQLite + "'");
            checkOrder();
        } else {
//            Decrease Status
            if (amountInt == 1) {
                sqLiteDatabase.delete("orderTABLE", "id" + "=" + idSQLite, null);
                checkOrder();
            } else {
                amountInt -= 1;
                sqLiteDatabase.execSQL("UPDATE orderTABLE SET Amount=" + "'" + Integer.toString(amountInt) + "'" + " WHERE id=" + "'" + idSQLite + "'");
                checkOrder();
            }

        }



    }

    private void foodRecyclerView(String idCategoryString) {
        RecyclerView recyclerView = getView().findViewById(R.id.recyclerFood);

        StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(2,
                LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(staggeredGridLayoutManager);

        MyConstant myConstant = new MyConstant();
        SharedPreferences sharedPreferences = getActivity()
                .getSharedPreferences(myConstant.getSharePreferFile(), Context.MODE_PRIVATE);
        String userLogin = sharedPreferences.getString("User", "");

        Log.d("25decV1", "idCat ==> " + idCategoryString);
        Log.d("25decV1", "userLogin ==> " + userLogin);

        final ArrayList<String> foodStringArrayList = new ArrayList<>();
        final ArrayList<String> priceStringArrayList = new ArrayList<>();
        final ArrayList<String> idFoodStringArrayList = new ArrayList<>();

        try {

            GetFoodWhereIdAndUser getFoodWhereIdAndUser = new GetFoodWhereIdAndUser(getActivity());
            getFoodWhereIdAndUser.execute(idCategoryString, userLogin, myConstant.getUrlGetFoodWhereIdAndUser());
            String jsonString = getFoodWhereIdAndUser.get();
            Log.d("25decV1", "jsonString123 ==> " + jsonString);

            if (jsonString.equals("null")) {
                recyclerView.setAdapter(null);
            }

            JSONArray jsonArray = new JSONArray(jsonString);
            for (int i = 0; i < jsonArray.length(); i += 1) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                foodStringArrayList.add(jsonObject.getString("pname"));
                priceStringArrayList.add(jsonObject.getString("price"));
                idFoodStringArrayList.add(jsonObject.getString("id"));
            }

            Log.d("25decV1", "food ==> " + foodStringArrayList.toString());
            FoodAdapter foodAdapter = new FoodAdapter(getActivity(), foodStringArrayList, priceStringArrayList, new OnClickItem() {
                @Override
                public void onClickItem(View view, int positions) {
                    Log.d("25DecV2", "Food ID Choose ==> " + idFoodStringArrayList.get(positions));
                    Log.d("25DecV2", "foodName Choose ==> " + foodStringArrayList.get(positions));
                    Log.d("25DecV2", "Food Price ==> " + priceStringArrayList.get(positions));

                    if (checkHaveFood(idFoodStringArrayList.get(positions))) {
//                            Increase Old Order
                        increaseAmountFood(idFoodStringArrayList.get(positions));
                    } else {
//                        Add New Order
                        myManageSQLite.addValueToSQLite(idFoodStringArrayList.get(positions),
                                foodStringArrayList.get(positions), priceStringArrayList.get(positions), "1");
                    }

                    checkOrder();


                }
            });

            recyclerView.setAdapter(foodAdapter);

        } catch (Exception e) {
            e.printStackTrace();
            Log.d("25decV2", "e ==> " + e.toString());
        }


    }

    private void increaseAmountFood(String idFoodString) {

        SQLiteDatabase sqLiteDatabase = getActivity()
                .openOrCreateDatabase(MyOpenHelper.database_name, Context.MODE_PRIVATE, null);
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM orderTABLE WHERE idFood = " + "'" + idFoodString + "'", null);
        cursor.moveToFirst();

        String amountString = cursor.getString(4);
        int amountInt = Integer.parseInt(amountString);
        amountInt += 1;
        amountString = Integer.toString(amountInt);
        cursor.close();

        sqLiteDatabase.execSQL("UPDATE orderTABLE SET Amount=" + "'" + amountString + "'" + " WHERE idFood=" + "'" + idFoodString + "'");

    }

    private boolean checkHaveFood(String idFoodString) {

        try {

            SQLiteDatabase sqLiteDatabase = getActivity().openOrCreateDatabase(MyOpenHelper.database_name, Context.MODE_PRIVATE, null);
            Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM orderTABLE", null);
            cursor.moveToFirst();

            boolean resule = false; // false Without IdFood in Database

            for (int i = 0; i < cursor.getCount(); i += 1) {

                if (idFoodString.equals(cursor.getString(1))) {
                    resule = true;
                }

                cursor.moveToNext();

            }
            cursor.close();

            Log.d("25decV4", "result ==> " + resule);
            return resule;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }


    }

    private void categoryRecyclerView() {

        RecyclerView recyclerView = getView().findViewById(R.id.recyclerCategory);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(),
                LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);

        try {

            MyConstant myConstant = new MyConstant();
            GetAllData getAllData = new GetAllData(getActivity());
            getAllData.execute(myConstant.getUrlGetCategoryString());

            String jsonString = getAllData.get();
            Log.d("24decV3", "jsonString ==> " + jsonString);

            ArrayList<String> categoryStringArrayList = new ArrayList<>();
            final ArrayList<String> idCategoryStringArrayList = new ArrayList<>();

            JSONArray jsonArray = new JSONArray(jsonString);
            for (int i = 0; i < jsonArray.length(); i += 1) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                categoryStringArrayList.add(jsonObject.getString("prcname"));
                idCategoryStringArrayList.add(jsonObject.getString("id"));
            }

            CategoryAdapter categoryAdapter = new CategoryAdapter(getActivity(), categoryStringArrayList, new OnClickItem() {
                @Override
                public void onClickItem(View view, int positions) {
                    Log.d("24decV3", "Position ==> " + positions);
                    foodRecyclerView(idCategoryStringArrayList.get(positions));
                }
            });

            recyclerView.setAdapter(categoryAdapter);

            foodRecyclerView(idCategoryStringArrayList.get(0));

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void receiveValue() {
        amountCustomerString = getArguments().getString("Amount");
        totalBillABoolean = getArguments().getBoolean("Bill");
        Log.d("24decV3", "amount ==> " + amountCustomerString);
        Log.d("24decV3", "Bill ==> " + totalBillABoolean);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_food, container, false);
    }

}