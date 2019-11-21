# Typescan (Optical Character Recognizer [Android])


## Introduction

Typescan is a mobile application designed and developed for the purpose of providing users a platform to digitalize a hand written text document into editable text format along with handwriting matching of users. The application is built using the Android SDK with Kotlin as the programming language and follows the MVVM (Model-View-Viewmodel) software architectural pattern. Convolutional Neural Network on TensorFlow framework and Keras API is used for training the machine learning model to recognize handwritten characters of the English alphabet and numeric digits. Each character data set contains 47 classes, which consists of uppercase characters, lowercase characters and numeric digits. Characters which look similar in lowercase and uppercase are merged into one class to minimize error. The purpose of this mobile application is to convert entire handwritten documents containing strings of non-touching characters into editable text format using OCR (Optical Character
Recognition). Along with this, the application also classifies each document based on its author by profiling each user’s handwriting and matching the document with the profile database.


## Get Started

Download the opencv for Android from [here](https://sourceforge.net/projects/opencvlibrary/files/opencv-android/) and copy "opencv-version-android-sdk/sdk/native/libs" to "projectRootFolder/app".
Open in Android Studio and run application.


## CNN Model

Please refer to [this repository](https://github.com/aayushsharma9/ocr-cnn) for CNN model training.


## Project Structure

    |→ Android Application
        |→ Main Module
            |→ Home Screen
                |→ List of scanned and converted documents
                    |→ List Item
                        |→ Preview Image
                        |→ Title
                        |→ Timestamp
                        |→ Author
                        |→ Share/delete actions
                    |→ Search Bar
                        |→ Update list by matching query substring
                    |→ Scan Document Button
                        |→ Opens camera API

        |→ Camera API
            |→ Detect Pages (Largest Rectangle in Image)
            |→ Auto focus on tap
            |→ Pre-processing of images
                |→ Resize image
                |→ Convert to grayscale
                |→ Convert to black and white
                |→ Invert color
                |→ Normalize color value
            |→ Conversion Algorithm on captured image
                |→ Sliding contour to detect single characters
                |→ Store the char images in HashMap corresponding to ASCII key
                |→ Get the ASCII character from image using TF Lite model
                |→ Store the character sequence of document into array

        |→ Document Viewer
            |→ Text editor
            |→ Initialize document viewport with character array scanned by camera
                |→ Enter author name (suggested for recurring authors)

        |→ Optical Character Recognizer Module
            |→ Assets folder contains the tflite model
            |→ Models
                |→ CharacterMap for each author
                |→ CharImage containing Hashmap of identical charactes for each character as a list
            |→ Utilities
                |→ Image Difference
                    |→ Acquire HashMap<Char, Array<Image>>
                    |→ Apply image matching on each char image for each author
                        |→ Average percentage match
                            |→ Create new user map if it is less than threshold
                            |→ Show matching percentage if it is more than threshold
                |→ Optical Character Detector
                    |→ Performs image processing on the received bitmap
                    |→ Performs segmentation into sentences, words and characters
                    |→ Add padding and resize each character
                    |→ Run inference on each Mat of character and store Mat and recognized character


## Data Flow Diagram

At application level, the data flow is as follows:
1. The input in the form of an image is provided by the user using the camera module to the text recognizer model for character recognition.
2. During this process the image is preprocessed, segmented and is converted to binary image.
3. This TensorFlow text recognizer model is trained on large dataset which helps in prediction of the corresponding character label.
4. After recognition a probability map in the form of a two-dimensional array is returned by the model.
5. Now using the probability map, we infer the word character by character.
6. The text obtained from inference module is written to a text file and saved in the designated application directory in the internal storage of the mobile phone.


![Data Flow Diagram](https://drive.google.com/uc?export=view&id=1Dh1XpFYTGBn-zf_svMd4lH9bps5YMQW2)
<p align=center>Data Flow Diagram</p>


## Image pre-processing


![Image pre-processing flow](https://drive.google.com/uc?export=view&id=1R1Pm-bOb09_2eCoEYMUOHtkL3KbW8ewY)
<p align=center>Image pre-processing flow</p>


## Screenshots

![Camera Module](https://drive.google.com/uc?export=view&id=1EqRa5_EjS7c9dc28cUkp6H5d7ET0wLiA)
<p align=left>Camera Module</p>


![Edge Detection in Camera Module](https://drive.google.com/uc?export=view&id=1qpX-7X_rGMwIFmB94mGMfQVBwiVpFYJI)
<p align=left>Edge Detection in Camera Module</p>


![Retake, rotate, delete and proceed functions for captured image](https://drive.google.com/uc?export=view&id=11Xr7WLy9hdikz9f9v94kVnFmrLPJaonc)
<p align=left>Retake, rotate, delete and proceed functions for captured image</p>


![Conversion into Editable Text](https://drive.google.com/uc?export=view&id=1I_J6v-dLb3-9cwhYplwYvnlgVJmHJ9B2)
<p align=left>Conversion into Editable Text</p>


![List view displaying all document items fetched from local database](https://drive.google.com/uc?export=view&id=122Jj8Tmf1_3Xgkji5dj0V1xWmqqVEyrM)
<p align=left>List view displaying all document items fetched from local database</p>



## Limitations

This project has been prepared with consideration to fulfil all the requirements and acceptable accuracy. However, some of the limitations are presented, these are as follows:

1. Data is only stored on the mobile device and is not backed up to cloud.
2. Current implementation does not differentiate between font sizes.
3. Images within the scanned document are not translated to the digital format.
4. Current segmentation algorithm only works for non-touching characters.
5. Current implementation of the matching algorithm does not differentiate between various writing styles, for example cursive writing and block writing styles.


## Conclusion & Future Scope

The project has been successfully completed according to the initial requirements and specifications. The machine learning model has been trained for character recognition from images using a modified version of NIST Special Dataset 19. The dataset has been modified to reduce character redundancy for similar looking letters such as lowercase and uppercase 0, m, w etc. and thus reducing the number of classes and conflicts within prediction set. An accuracy of 91% has been obtained by training the model for 30 epochs with Convolutional Neural Network having 19 layers. The model is then loaded to the Android application for real time character recognition. The camera module is used for capturing the document images, which are segmented and pre-processed for the model to predict the corresponding character classes. The accuracy is further improved by using an English dictionary for all the recognized words and correcting them after the OCR process. The character map of the specified author is then saved and matched with existing author maps and in case of a match, the author field is assigned. The backend of the application is implemented using Model View Viewmodel architectural pattern within Android SDK using Kotlin language. Current version of the application recognizes non-touching characters in a word, and saves the output file in local storage. With room for improvement in the application, the following are the functionalities and modules to be implemented during the major phase of the project:

1. A neural network approach for character segmentation of words with connected letters.
2. To deploy the machine learning model to a RESTful API for smoother updates.
3. Hosting the database to a cloud-based service.
4. Maintaining a change history for previously saved and edited documents.
5. Adding classes for special characters and their corresponding images to the dataset.


## License

This repository is [GNU GENERAL PUBLIC LICENSE Version 3](LICENSE) licensed.

