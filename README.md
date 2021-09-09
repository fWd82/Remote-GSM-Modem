# Remote GSM Modem
Send SMS from **Remote Server** using your **Android Phone**. Use your Phone as GSM Modem for your Web Apps hosted remotely.


# Introduction
We pay huge amounts to companies providing `Short Messaging Services` and one single message cost a lot. For startups or testers it is hard for them to pay for huge services they don't even need. Why would we pay a lot while we can turn our smart phone as GSM Modem. 

## How it works
As your app is hosted remotely on server. You may create yourself API or use our [pre-built API](https://github.com/fWd82/Remote-GSM-Modem-API/) for sending messages. You will host that API on your server. By using the API you can send SMS just by calling `HTTP GET` request from your browser, app or even microcontrollers. The app will look for any new entry and will send message to specified `user` with `message` mentioned. The API will callback with `true` or `false` and will change the status entry for `pending` or `sent`


## How it is different from GSM Modem
Mr. [Sadiq Odho](https://github.com/sadiqodho) worked on [GSM Modem (SMS)](https://github.com/sadiqodho/GSM-Helper-Tool) and is used widely. Unfortunately it works only on local network. Your App must be on local machine and has same area network with your Android Phone. Alternativaly Remote GSM Modem works on Remote networks and even work with your App hosted locally. 

# Setting Up API 
Detailed discussion are here on how to set up this api [HERE](https://github.com/fWd82/Remote-GSM-Modem-API/blob/main/README.md)

## Database

- Create Database in **MySQL® Databases**, name it whatever you want.
- Go to **phpMyAdmin** in your cpanel and execute [this sql](https://github.com/fWd82/Remote-GSM-Modem-API/blob/main/gsm_api.sql) script
- or You can create even new `table` name it: `users_mobile`

And inside create these columns: 

    DB Name: YOUR_DB_NAME
    Table Name: users_mobile  
    
    Columns:
    id int(11)
    name	varchar(255) 
    mobile	varchar(255) 
    message	varchar(255) 
    status	tinyint(10) 
    timestamp datetime

Now you are done with database part.

## API
- Now Download API from [HERE](https://github.com/fWd82/Remote-GSM-Modem-API/) which is written in PHP. 
- Go to your **File Manager** > **public_html**  create new directory/folder, name it **api** (or _GSM_API_) and paste content of API that you have downloaded in first step.
- Change credentials in file: `config.php` [here](https://github.com/fWd82/Remote-GSM-Modem-API/blob/main/config.php#L5) on line number `4`, `5`, `6` according to your settings.

At this moment your DB & API is ready to test.

You can check your API home page: 

    exampleurl.com/api/

## HTTP API Calls
### Sending Message
Just call:
    
    https://exampleurl.com/api/gsm_api.php?action=insert&name=NAME&mobile=+9XXXXXXXXXX&message=ANY_MESSAGE&status=0

Pass these parameters:

    name STRING
    mobile STRING
    message STRING
    status BOOLEAN

Sometime we don't want to send message to some record that's why you can just pass `status=1`  

### Updating value of Status
Change value of status from `0` to `1` of record with `id=1` as below:

    https://exampleurl.com/api/gsm_api.php?action=update&id=1&status=1


### General API Calls for App

    Fetch New Data (those status values are 0 | See if any message is pending that we need to send) 
    https://exampleurl.com/api/gsm_api.php?action=fetch_new

    Update: [Change value from 0 to 1 | Send status is 0 so if successfully sent change value from 0 to 1]
    https://exampleurl.com/api/gsm_api.php?action=update&id=1&status=1
    
    INSERT | SEND MESSAGE - By inserting new row our app will send message to specified mobile number:
    https://exampleurl.com/api/gsm_api.php?action=insert&name=NAME&mobile=+9XXXXXXXXXX&message=ANY_MESSAGE&status=0


    // Below aren't needed in our app but you can use it anyway
    Fetch All: [ALL]
    https://exampleurl.com/api/gsm_api.php?action=fetch_all
    
    [Delete Message / Del Record from DB]
    http://exampleurl/api/gsm_api.php?action=delete&id=2


You can delete records after it hit `500` or some certain limit by `cron job` or whatever method you prefer just to not make your database bulky.


## CONTRIBUTION
Any contribution is welcome. Please push your changes to branch `development`

### Clone this repo
    git clone https://github.com/fWd82/Remote-GSM-Modem.git


## SCREENSHOTS

![Screenshot_1631220087](https://user-images.githubusercontent.com/19715150/132762190-3779febc-0d1a-4866-b031-fbbe364abea7.png)
![Screenshot_1631220122](https://user-images.githubusercontent.com/19715150/132762203-4a3b3b1d-5314-40c0-836a-e02f9f4ff362.png)
![Screenshot_1631220133](https://user-images.githubusercontent.com/19715150/132762215-fb25634b-47f5-4680-af83-b73c6ea8aedf.png)
![Screenshot_1631220156](https://user-images.githubusercontent.com/19715150/132762222-b62d5d09-21bf-4e7a-84fb-7767be921e65.png)
![Screenshot_1631220183](https://user-images.githubusercontent.com/19715150/132762223-3f1e69a6-4238-4dad-afc4-088b35128c5b.png)
![Screenshot_1631220231](https://user-images.githubusercontent.com/19715150/132762227-615c3b8d-e3a6-4f5e-bcc0-988c6c4d75ec.png)
![Screenshot_1631220255](https://user-images.githubusercontent.com/19715150/132762229-fa6429f7-e68b-421d-8d5a-23b11ab51978.png)


