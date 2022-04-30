package com.kudu.shopapp.firestore

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import android.util.Log
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.kudu.shopapp.activities.*
import com.kudu.shopapp.fragments.DashboardFragment
import com.kudu.shopapp.fragments.OrdersFragment
import com.kudu.shopapp.fragments.ProductsFragment
import com.kudu.shopapp.fragments.SoldProductsFragment
import com.kudu.shopapp.model.*
import com.kudu.shopapp.util.Constants

class Firestore {

    private val mFireStore = FirebaseFirestore.getInstance()

    fun registerUser(activity: RegisterActivity, userInfo: User) {

        // The "users" is a collection name
//        mFireStore.collection("users")
        mFireStore.collection(Constants.USERS)// using constant for a clean work of not getting crashed
            //Document ID for users fields. Here the document is the User ID
            .document(userInfo.id)
            //here the userInfo are fields and the SetOption is set to merge. It is if merge is to done later for replacing fields
            .set(userInfo, SetOptions.merge())
            .addOnSuccessListener {

                //function of base activity for transferring the result to it
                activity.userRegistrationSuccess()
            }
            .addOnFailureListener { e ->
                activity.hideProgressDialog()
                Log.e(
                    activity.javaClass.simpleName,
                    "Error while registering the user",
                    e
                ) // need to know further about this
            }
    }

    fun getCurrentUserId(): String {
        // an instance of current user using firebase auth
        val currentUser = FirebaseAuth.getInstance().currentUser

        //variable to assign the currentUserId if it is not null or else it will be blank.
        var currentUserId = ""
        if (currentUser != null) {
            currentUserId = currentUser.uid
        }
        return currentUserId
    }

    fun getUserDetails(activity: Activity) {
        //passing the collection name from which data is to be retrieved
        mFireStore.collection(Constants.USERS)
            .document(getCurrentUserId())
            .get()
            .addOnSuccessListener { document ->
                Log.i(activity.javaClass.simpleName, document.toString())

                val user = document.toObject(User::class.java)!!

                val sharedPreferences = activity.getSharedPreferences(
                    Constants.SHOPAPP_PREFERENCES,
                    Context.MODE_PRIVATE
                )

                val editor: SharedPreferences.Editor = sharedPreferences.edit()
                //Key: logged_in_username
                //value: firstname and lastName
                editor.putString(Constants.LOGGED_IN_USERNAME, "${user.firstName} ${user.lastName}")
                editor.apply()

                when (activity) {
                    is LoginActivity -> {
                        //calling a function of base activity for transferring the result to it
                        activity.userLoggedInSuccess(user)
                    }
                    is SettingsActivity -> {
                        activity.userDetailsSuccess(user)
                    }
                }
            }
            .addOnFailureListener { e ->
                when (activity) {
                    is LoginActivity -> {
                        activity.hideProgressDialog()
                    }
                    is SettingsActivity -> {
                        activity.hideProgressDialog()
                    }
                }
                Log.e(activity.javaClass.simpleName, "Error while getting user details.", e)
            }

    }

    fun updateUserProfileData(activity: Activity, userHashMap: HashMap<String, Any>) {
        mFireStore.collection(Constants.USERS)
            .document(getCurrentUserId())
            .update(userHashMap)
            .addOnSuccessListener {
                when (activity) {
                    is UserProfileActivity -> {
                        activity.userProfileUpdateSuccess()
                    }
                }
            }
            .addOnFailureListener { e ->
                when (activity) {
                    is UserProfileActivity -> {
                        activity.hideProgressDialog()
                    }
                }
                Log.e(activity.javaClass.simpleName, "Error while updating user details.", e)
            }
    }

    fun uploadImageToCloudStorage(activity: Activity, imageFileURI: Uri?, imageType: String) {
        /* val sRef: StorageReference =
             FirebaseStorage.getInstance().reference.child("user-profile-images/" + imageType + System.currentTimeMillis() + "."
                     + Constants.getFileExtension(
                 activity,
                 imageFileURI))*/
        val sRef: StorageReference =
            FirebaseStorage.getInstance().reference.child(imageType + System.currentTimeMillis() + "." + Constants.getFileExtension(
                activity,
                imageFileURI))
        //adding the file to reference
        sRef.putFile(imageFileURI!!)
            .addOnSuccessListener { taskSnapshot ->
                // The image upload is success
                Log.e("Firebase Image URL",
                    taskSnapshot.metadata!!.reference!!.downloadUrl.toString()
                )
                // Get the downloadable url from the task snapshot
                taskSnapshot.metadata!!.reference!!.downloadUrl
                    .addOnSuccessListener { uri ->
                        Log.e("Downloadable Image URL", uri.toString())
                        // Here call a function of base activity for transferring the result to it.
                        when (activity) {
                            is UserProfileActivity -> {
                                activity.imageUploadSuccess(uri.toString())
                            }
                            is AddProductActivity -> {
                                activity.imageUploadSuccess(uri.toString())
                            }
                        }
                    }
            }
            .addOnFailureListener { exception ->
                // Hide the progress dialog if there is any error. And print the error in log.
                when (activity) {
                    is UserProfileActivity -> {
                        activity.hideProgressDialog()
                    }
                    is AddProductActivity -> {
                        activity.hideProgressDialog()
                    }
                }
                Log.e(activity.javaClass.simpleName, exception.message, exception
                )
            }
    }

    fun uploadProductDetails(activity: AddProductActivity, productInfo: Product) {
        mFireStore.collection(Constants.PRODUCTS)
            .document()
            .set(productInfo, SetOptions.merge())
            .addOnSuccessListener {
                activity.productUploadSuccess()
            }
            .addOnFailureListener { e ->
                activity.hideProgressDialog()
                Log.e(activity.javaClass.simpleName, "Error uploading product details...", e)
            }
    }

    fun getProductList(fragment: Fragment) {
        mFireStore.collection(Constants.PRODUCTS)
            .whereEqualTo(Constants.USER_ID, getCurrentUserId())
            .get()
            .addOnSuccessListener { document ->
                Log.e("Product List", document.documents.toString())
                val productList: ArrayList<Product> = ArrayList()
                for (i in document.documents) {
                    val product = i.toObject(Product::class.java)
                    product!!.id = i.id
                    productList.add(product)
                }
                when (fragment) {
                    is ProductsFragment -> {
                        fragment.successProductListFromFirestore(productList)
                    }
                }
            }
            .addOnFailureListener { e ->
                Log.e(fragment.javaClass.simpleName, "Error while getting product item list", e)
            }
    }

    fun getProductDetails(activity: ProductDetailsActivity, productId: String) {
        mFireStore.collection(Constants.PRODUCTS)
            .document(productId)
            .get()
            .addOnSuccessListener { document ->
                Log.e(activity.javaClass.simpleName, document.toString())
                val product = document.toObject(Product::class.java)
                if (product != null) {
                    activity.productDetailsSuccess(product)
                }
            }
            .addOnFailureListener { e ->
                activity.hideProgressDialog()
                Log.e(activity.javaClass.simpleName,
                    "Error while getting product details.",
                    e)
            }
    }

    fun addToCartItems(activity: ProductDetailsActivity, addToCart: CartItem) {
        mFireStore.collection(Constants.CART_ITEMS)
            .document()
            .set(addToCart, SetOptions.merge())
            .addOnSuccessListener {
                activity.addToCartSuccess()
            }
            .addOnFailureListener { e ->
                activity.hideProgressDialog()
                Log.e(activity.javaClass.simpleName, "Error while creating the cart items", e)
            }
    }

    fun getCartList(activity: Activity) {
        mFireStore.collection(Constants.CART_ITEMS)
            .whereEqualTo(Constants.USER_ID, getCurrentUserId())
            .get()
            .addOnSuccessListener { document ->
                Log.e(activity.javaClass.simpleName, document.documents.toString())
                val list: ArrayList<CartItem> = ArrayList()
                for (i in document.documents) {
                    val cartItem = i.toObject(CartItem::class.java)!!
                    cartItem.id = i.id
                    list.add(cartItem)
                }
                when (activity) {
                    is CartListActivity -> {
                        activity.successCartItemsList(list)
                    }
                    is CheckoutActivity -> {
                        activity.successCartItemsList(list)
                    }
                }
            }
            .addOnFailureListener { e ->
                when (activity) {
                    is CartListActivity -> {
                        activity.hideProgressDialog()
                    }
                    is CheckoutActivity -> {
                        activity.hideProgressDialog()
                    }
                }
                Log.e(activity.javaClass.simpleName, "Error while getting the cart list items", e)
            }
    }

    fun updateMyCart(context: Context, cart_id: String, itemHashMap: HashMap<String, Any>) {
        mFireStore.collection(Constants.CART_ITEMS)
            .document(cart_id)
            .update(itemHashMap)
            .addOnSuccessListener {
                when (context) {
                    is CartListActivity -> {
                        context.itemUpdateSuccess()
                    }
                }
            }
            .addOnFailureListener { e ->
                when (context) {
                    is CartListActivity -> {
                        context.hideProgressDialog()
                    }
                }
                Log.e(context.javaClass.simpleName, "Error while updating cart items", e)
            }
    }

    fun checkIfItemExistsInCart(activity: ProductDetailsActivity, productId: String) {
        mFireStore.collection(Constants.CART_ITEMS)
            .whereEqualTo(Constants.USER_ID, getCurrentUserId())
            .whereEqualTo(Constants.PRODUCT_ID, productId)
            .get()
            .addOnSuccessListener { document ->
                Log.e(activity.javaClass.simpleName, document.documents.toString())
                if (document.documents.size > 0) {
                    activity.checkProductExistsInCart()
                } else {
                    activity.hideProgressDialog()
                }
            }
            .addOnFailureListener { e ->
                activity.hideProgressDialog()
                Log.e(activity.javaClass.simpleName, "Error while checking existing cart list", e)
            }
    }

    fun updateAllDetails(activity: CheckoutActivity, cartList: ArrayList<CartItem>, order: Order) {
        val writeBatch = mFireStore.batch() // batch allows to do multiple works at a time

        for (cartItem in cartList) {
            /*val productHashMap = HashMap<String, Any>()
            productHashMap[Constants.STOCK_QUANTITY] =
                (cartItem.stock_quantity.toInt() - cartItem.cart_quantity.toInt()).toString()*/
            val soldProduct = SoldProduct(
                cartItem.product_owner_id,
                cartItem.title,
                cartItem.price,
                cartItem.cart_quantity,
                cartItem.image,
                order.title,
                order.order_date_time,
                order.sub_total_amount,
                order.shipping_charge,
                order.total_amount,
                order.address
            )

            val documentRef = mFireStore.collection(Constants.SOLD_PRODUCTS)
                .document(cartItem.product_id)

//            writeBatch.update(documentRef, soldProduct)
            writeBatch.set(documentRef, soldProduct)
        }
        //deleting cartItem
        for (cartItem in cartList) {
            val documentRef = mFireStore.collection(Constants.CART_ITEMS)
                .document(cartItem.id)
            writeBatch.delete(documentRef)
        }

        writeBatch.commit()
            .addOnSuccessListener {
                activity.allDetailsUpdatedSuccessfully()
            }
            .addOnFailureListener { e ->
                activity.hideProgressDialog()
                Log.e(activity.javaClass.simpleName,
                    "Error while updating all the details after order placed",
                    e)
            }
    }

    fun getSoldProductsList(fragment: SoldProductsFragment) {
        mFireStore.collection(Constants.SOLD_PRODUCTS)
            .whereEqualTo(Constants.USER_ID, getCurrentUserId())
            .get()
            .addOnSuccessListener { document ->
                Log.e(fragment.javaClass.simpleName, document.documents.toString())
                val list: ArrayList<SoldProduct> = ArrayList()
                for (i in document.documents) {
                    val soldProduct = i.toObject(SoldProduct::class.java)!!
                    soldProduct.id = i.id
                    list.add(soldProduct)
                }
                fragment.successSoldProductsList(list)
            }
            .addOnFailureListener { e ->
                fragment.hideProgressDialog()
                Log.e(fragment.javaClass.simpleName,
                    "Error while getting the list of sold products",
                    e)
            }
    }

    fun getMyOrdersList(fragment: OrdersFragment) {
        mFireStore.collection(Constants.ORDERS)
            .whereEqualTo(Constants.USER_ID, getCurrentUserId())
            .get()
            .addOnSuccessListener { document ->
                val list: ArrayList<Order> = ArrayList()
                for (i in document.documents) {
                    val orderItem = i.toObject(Order::class.java)!!
                    orderItem.id = i.id
                    list.add(orderItem)
                }
                fragment.populateOrdersList(list)
            }
            .addOnFailureListener { e ->
                fragment.hideProgressDialog()
                Log.e(fragment.javaClass.simpleName, "Error while getting orders list", e)
            }
    }

    fun placeOrder(activity: CheckoutActivity, order: Order) {
        mFireStore.collection(Constants.ORDERS)
            .document()
            .set(order, SetOptions.merge())
            .addOnSuccessListener {
                activity.orderPlacedSuccess()
            }
            .addOnFailureListener { e ->
                activity.hideProgressDialog()
                Log.e(activity.javaClass.simpleName, "Error while placing your order", e)
            }
    }

    fun deleteProduct(fragment: ProductsFragment, productId: String) {
        mFireStore.collection(Constants.PRODUCTS)
            .document(productId)
            .delete()
            .addOnSuccessListener {
                fragment.productDeleteSuccess()
            }
            .addOnFailureListener { e ->
                fragment.hideProgressDialog()
                Log.e(fragment.requireActivity().javaClass.simpleName,
                    "Error while deleting the product.",
                    e)

            }
    }

    fun removeItemFromCart(context: Context, cart_id: String) {
        mFireStore.collection(Constants.CART_ITEMS)
            .document(cart_id)
            .delete()
            .addOnSuccessListener {
                when (context) {
                    is CartListActivity -> {
                        context.itemRemoveSuccess()
                    }
                }
            }
            .addOnFailureListener { e ->
                when (context) {
                    is CartListActivity -> {
                        context.hideProgressDialog()
                    }
                }
                Log.e(context.javaClass.simpleName, "Error while removing the item from cart", e)
            }
    }

    fun getAllProductsList(activity: Activity) {
        mFireStore.collection(Constants.PRODUCTS)
            .get()
            .addOnSuccessListener { document ->
                Log.e("Products List", document.documents.toString())
                val productsList: ArrayList<Product> = ArrayList()
                for (i in document.documents) {
                    val product = i.toObject(Product::class.java)
                    product!!.id = i.id

                    productsList.add(product)
                }
                when (activity) {
                    is CartListActivity -> {
                        activity.successProductsListFromFirestore(productsList)
                    }
                    is CheckoutActivity -> {
                        activity.successProductsListFromFirestore(productsList)
                    }
                }
            }
            .addOnFailureListener { e ->
                when (activity) {
                    is CartListActivity -> {
                        activity.hideProgressDialog()
                    }
                    is CheckoutActivity -> {
                        activity.hideProgressDialog()
                    }
                }
                Log.e("Get Product List", "Error while getting all product list", e)
            }
    }

    fun getDashboardItemsList(fragment: DashboardFragment) {
        mFireStore.collection(Constants.PRODUCTS)
            .get()
            .addOnSuccessListener { document ->
                Log.e("Product List", document.documents.toString())
                val productList: ArrayList<Product> = ArrayList()
                for (i in document.documents) {
                    val product = i.toObject(Product::class.java)
                    product!!.id = i.id
                    productList.add(product)
                }
                fragment.successDashboardItemsList(productList)
            }
            .addOnFailureListener { e ->
                fragment.hideProgressDialog()
                Log.e(fragment.javaClass.simpleName, "Error while getting dashboard item list", e)
            }
    }

    fun deleteAddress(activity: AddressListActivity, addressId: String) {
        mFireStore.collection(Constants.ADDRESSES)
            .document(addressId)
            .delete()
            .addOnSuccessListener {
                activity.deleteAddressSuccess()
            }
            .addOnFailureListener { e ->
                activity.hideProgressDialog()
                Log.e(activity.javaClass.simpleName, "Error while deleting address", e)
            }
    }

    fun updateAddress(activity: AddEditAddressActivity, addressInfo: Address, addressId: String) {
        mFireStore.collection(Constants.ADDRESSES)
            .document(addressId)
            .set(addressInfo, SetOptions.merge())
            .addOnSuccessListener {
                activity.addUpdateAddressSuccess()
            }
            .addOnFailureListener { e ->
                activity.hideProgressDialog()
                Log.e(activity.javaClass.simpleName, "Error while updating address", e)
            }
    }

    fun getAddressList(activity: AddressListActivity) {
        mFireStore.collection(Constants.ADDRESSES)
            .whereEqualTo(Constants.USER_ID, getCurrentUserId())
            .get()
            .addOnSuccessListener { document ->
                Log.e(activity.javaClass.simpleName, document.documents.toString())

                //creating an instance for address arraylist
                val addressList: ArrayList<Address> = ArrayList()
                for (i in document.documents) {
                    val address = i.toObject(Address::class.java)!!
                    address.id = i.id

                    addressList.add(address)
                }
                activity.successAddressListFromFirestore(addressList)
            }
            .addOnFailureListener { e ->
                activity.hideProgressDialog()
                Log.e(activity.javaClass.simpleName, "Error while getting address", e)
            }
    }

    fun addAddress(activity: AddEditAddressActivity, addressInfo: Address) {
        mFireStore.collection(Constants.ADDRESSES)
            .document()
            .set(addressInfo, SetOptions.merge())
            .addOnSuccessListener {
                activity.addUpdateAddressSuccess()
            }
            .addOnFailureListener { e ->
                activity.hideProgressDialog()
                Log.e(activity.javaClass.simpleName, "Error while adding the address", e)
            }
    }

}