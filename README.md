"# open-cart-bulk-upload" 

The idea for this is just t make it a little easier to bulk upload
products to OpenCart.

Instructions:

1) Fill in the ProductDetails.xlsx spreadsheet with basic product data.
2) Fill in connection and path data in the Main.java file
3) Run but note any missing images. A placeholder image will be created for any missing product images.
4) Take extracted images and munually place them in the required folder on the OpenCart installation.


To Undo Actions:

delete from oc_product where oc_product.product_id >= ?;
delete from oc_product_description where oc_product_description.product_id >= ?;
delete from oc_product_to_category where oc_product_to_category.product_id >= ?;

delete from oc_product_to_layout where oc_product_to_layout.product_id >= ?;
delete from oc_product_to_store where oc_product_to_store.product_id >= ??;
delete from oc_product_image where oc_product_image.product_id >= ??;

where id is one of the products just inserted (get that from created date on record).