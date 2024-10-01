# YardSaleFlipper

## How to Use

This is an app for users to use to flip items they find at a yard sale for a profit. When opening the app, the user is greeted with a camera viewfinder. The user can then take a picture of an item at a yard sale using this image. After taking the picture, the user is brought to another screen where they can see what it was identified as, and then a list of possible prices for that product. The app lists eBay listings of the product  and their prices. Alternatively, the user can type in the product name into the search. If the user clicks on the listing, they can see a picture of the product. 

## Project

This was made in Android Studio. The app uses the ImageCapture class in Java to take a picture. The camera, eBay listing, and item description screens are 3 separate activities. The app uses a Hugging Face model (https://huggingface.co/google/vit-base-patch16-224) to identify the object in the image. It then uses the eBay API to search through the eBay listings and display the items in a ListView. It parses the JSON output of the listing to get the necessary information. The app uses the sandbox version of the eBay API, so many of the listings are test listings.
