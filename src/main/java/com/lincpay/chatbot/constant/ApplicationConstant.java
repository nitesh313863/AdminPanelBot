package com.lincpay.chatbot.constant;


public interface ApplicationConstant {

	public static final String BANK = "COS";
	public static final String YESBANK = "YES";
	public static final String COMPANY = "SL";
	public static final String INTERNALSERVERERROR = "internal server error";
	public static final int INTERNALSERVER_ERROR = 600;
	public static final int OK = 200;
	public static final int NOT_ACCEPTED = 306;
	public static final String CREATED = "ADDED SUCCESSFULLY";
	public static final int NOTFOUND = 304;
	public static final int ALREADYNOTFOUND = 308;
	public static final int SERVICECHARGENOTFOUND = 309;
	public static final String MERCHANTNOTFOUND = "Merchant Not found";
	public static final String ENTITYNOTFOUND = "Entity Not found";

	public static final int ALREADYFOUND = 403;

	public static final int LowBalance = 202;

	public static final String BusinessType_Not_Exist = "business type not exist";
	public static final String BAD_REQUEST = "500";
	public static final String USER_UNAUTHORIZED = "500";
	public static final String SUCCESS = "Success";
	public static final String ALL_COMPANYDETAILS = "all company details";
	public static final String COMPANY_DETAILS_NOT_EXIST = "Company details not exist";
	public static final String ALL_COMPANY = "Ok";
	public static final String MERCHANT_NOT_EXIST = "Merchant not found ";
	public static final String NOT_FOUND = "no Data found";
	public static final String INTERNAL_SERVER_ERROR = "something went wrong";
	public static final String CompanyType_Not_Exist = "company type not exist";
	public static final String PINCODE_NOT_FOUND = "pincode not exist";
	public static final String ALL_PAYMENTTYPE = "all payment type";
	public static final String PAYMENT_TYPE_NOT_EXIST = " payment type not exist";
	public static final String Merchant_Not_Exist = "merchant not exist";
	public static final String Not_Accepted = "Not Accepted";
	public static final String Null = null;
	public static final String Failed = "failed";

	public static final int ACCEPTED = 302;
	public static final int NOTVERIFIED = 307;
	public static final int USERUNAUTHORIZED = 501;
	public static final int BADREQUEST = 300;
	public static final int INTERNALSERVERERROR1 = 300;
	public static final String NotSaved = "Data not saved";

	public static final String LOGIN_SUCCESS = "Login Success";
	public static final String DASHBOARD_NOT_EXIST = "Dashboard Data Not Found";
	public static final String REPORT_NOT_EXIST = "Report Data Not Found";
	public static final String USER_NOT_EXIST = " User Not Found";

	public static final String STATE_EXIST = "State already exist";
	public static final String STATE_NOT_EXIST = "State not exist";
	public static final String STATE_ADDED = "New state added";
	public static final String STATE_UPDATED = "New state updated";
	public static final String ALL_STATE = "ok";

	public static final String CITY_EXIST = "City already exist";
	public static final String CITY_NOT_EXIST = "city not exist";
	public static final String CITY_ADDED = "New city added";
	public static final String CITY_UPDATED = "New city updated";
	public static final String ALL_CITY = "ok";
	public static final String ALL_BUSINESS = "ok";
	public static final String BUSINESS_TYPE_NOT_EXIST = "not exist";

	public static final String DONE = "Done";
	public static final String ALL_ADDRESS = "All Address";
	public static final String ALL_DATA = "All Data";
	public static final String DENIED = "Denied";
	public static final String SOMETHING_WENT_WROUNG = "Something Went Wroung";
	public static final String ALL_MERCAHNT_DETAILS = "All Merchant Details";
	public static final String DETAILS_NOT_FOUND = "No Details Found";
	public static final String FAILED = "failed";
	public static final String SUCCESS_MESSAGE = "Guaranty Amount saved successfully";
	public static final String ERROR_MESSAGE = "Guaranty Amount not saved";

	public static final String INVALID_CREDENTIALS_MESSAGE = "Invalid userName or Password";
	public static final String INVALID_CREDENTIALS_STATUS = "INVALID_CREDENTIALS";
	public static final int INVALID_CREDENTIALS_CODE = 401; // Unauthorized

	public static final String LOGOUT_SUCCESS_MESSAGE = "Logout Success";
	public static final String TOTAL_COMMISSION_MESSAGE = "Total Commission";
	public static final String NO_COMMISSION_FOUND_MESSAGE = "No commission found for the given MID";

	public static final String TRANSACTION_SAVED = "Transaction saved";
	public static final String TRANSACTION_NOT_SAVED = "Transaction not saved";
	public static final String TRANSACTION_UPDATED = "Transaction Updated";
	public static final String TRANSACTION_NOT_UPDATED = "Transaction not updated";
	public static final String ALL_MERCHANT_DEPOSIT = "All merchant deposit";

	public static final String AMOUNT_ADDED = "Amount Added";
	public static final String AMOUNT_NOT_ADDED = "Amount not Added";
	public static final String AMOUNT_NOT_UPDATED = "Amount not Updated";
	public static final String ENTRIES_NOT_FOUND = "Entries not found";
	public static final String AMOUNT_NOT_FOUND = "Amount not found";
	public static final String ALL_AMOUNT = "All Amount";

	public static final String INVALID_STORAGE_LOCATION = "Invalid storage location";
	public static final String FILE_UPLOADED_SUCCESSFULLY = "File uploaded successfully";
	public static final String ERROR_UPLOADING_FILE = "Error uploading the file";
	public static final String PLEASE_UPLOAD_PDF_FILE = "Please upload a PDF file";

	public static final String BUSINESS_NOT_FOUND = "Business not found with id";
	public static final String OTP_SEND_SUCCESSFULLY = "Otp Send successfully";
	public static final String OTP_SENDED = "Otp sended";
	public static final String INVALID_AADHAR_NUMBER = "Invalid Aadhar number";
	public static final String PLEASE_ENTER_CORRECT_AADHAR_NUMBER = "Please enter correct Aadhar number";
	public static final String AADHAAR_VERIFIED = "Aadhaar verified";
	public static final String INVALID_OTP = "Invalid OTP";
	public static final String PLEASE_ENTER_CORRECT_OTP = "Please enter correct OTP";
	public static final String VERIFIED = "Verified";
	public static final String BANK_ACCOUNT_NOT_FOUND = "Bank account not found";
	public static final String AADHAAR_UPDATED = "Aadhaar Updated";
	public static final String UPI_NOT_VERIFIED = "UPI not verified";
	public static final String BANK_DATA_NOT_FOUND = "Bank Data not found";
	public static final String EKYC_USERS_NOT_FOUND = "EKYC users not found";
	public static final String NOT_VERIFIED = "Not Verified";
	public static final String PANNUMBER_NOT_VERIFIED = "PanNumber not verified";

	public static final String MAIL_SENT_SUCCESSFULLY = "Mail sent successfully";
	public static final String PLEASE_SEND_CORRECT_EMAIL = "Please send correct email";
	public static final String PLEASE_INSERT_CORRECT_OTP = "Please insert correct OTP";
	public static final String OTP_MATCHED = "Otp matched";

	public static final String LOGS_SAVED = "Logs saved";
	public static final String LOGS_NOT_SAVED = "Logs not saved";
	public static final String ALL_LOGS = "All Logs";
	public static final String LOGOUT_FAILED = "Logout failed";
	public static final String CANCEL = "Cancel";

	public static final String MERCHANT_SAVED = "Merchant saved";
	public static final String MERCHANT_CREATED = "Merchant created";
	public static final String MOBILE_NUMBER_NOT_VERIFIED = "Mobile number not verified";
	public static final String EMAIL_NOT_VERIFIED = "Email not verified";
	public static final String ALREADY_VERIFIED = "Already verified";
	public static final String MERCHANT_UPDATED = "Merchant Updated";
	public static final String BANK_DETAILS_SAVED = "Bank Details Saved";
	public static final String MERCHANT_BANK_DETAILS_UPDATED = "Merchant Bank Details Updated";
	public static final String MERCHANT_BANK_DETAILS_NOT_SAVED = "Merchant bankDetails not saved";
	public static final String PASSWORD_CHANGED = "Password was changed";
	public static final String CURRENT_PASSWORD_INCORRECT = "Current password is incorrect";
	public static final String ALL_REFERRED_MERCHANT = "All referred merchant";
	public static final String NO_REFERRED_MERCHANT_FOUND = "No referred merchant found";

	public static final String MERCHANT_NOT_SAVED = "Merchant not saved";
	public static final String NOT_SAVED = "Not Saved";

	public static final String ACCOUNT_COMPANY_NAME_MISMATCH = "AccoutBeneficiaryName and Company name is not matched";

	public static final String MERCHANT_NOT_FOUND = "Merchant not found";

	public static final String DOCUMENT_SAVED = "Document saved";
	public static final String CORRECT_FILE = "Correct File";

	public static final String FILE_NOT_UPLOADED = "File not uploaded";
	public static final String DATA_MISSING_OR_INCORRECT = "Data is missing or incorrect";
	public static final String MISSING_DATA = "Missing data:";
	public static final String FILE_NOT_UPLOADED_SUCCESS = "File not uploaded Success";
	public static final String SOME_DATA_MISSING_OR_INCORRECT = "Some data is missing or incorrect";
	public static final String FILE_NOT_UPLOADED_ERROR = "File not uploaded";
	public static final String ERROR_READING_FILE = "Error occurred while reading the file";
	public static final String UNKNOWN_ERROR = "Unknown error";

	public static final String PAYOUT_REQUEST_SUBMITTED = "Payout Request Submitted";
	public static final String PAYOUTS_REQUEST_NOT_SUBMITTED = "Payouts Request not Submitted";
	public static final String ALL_PAYOUT_REQUEST = "All Payout Request";
	public static final String PAYOUTS_REQUEST_NOT_AVAILABLE = "Payouts Request not available";
	public static final String DELETED = "Deleted";

	public static final String PLEASE_INSERT_VALID_PAYLOAD = "Please insert valid payload";
	public static final String PAYOUTS_SUBMITTED = "Payouts Submitted";
	public static final String PAYOUTS_NOT_SUBMITTED = "Payouts not Submitted";
	public static final String PAYOUTS_NOT_AVAILABLE = "Payouts not available";
	public static final String TRANSACTION_STATUS_DETAILS = "Transaction status Details";

	public static final String ALL_PAYOUT_DETAILS = "All Payout Details";
	public static final String NO_RECORD_FOUND = "No Record Found";
	public static final String NO_DATA_FOUND = "No data found";

	public static final String SUB_USER_CREATED = "SubUser Created";
	public static final String SUB_USER_NOT_CREATED = "SubUser not created";
	public static final String SUB_USER_AND_PRIVILEGES_UPDATED = "Sub-user and privileges updated successfully";
	public static final String SUB_USER_UPDATE_FAILED = "Sub-user update failed";
	public static final String SUB_USER_NOT_FOUND = "Sub-user not found";
	public static final String ALL_SUB_USERS = "All Sub-Users";

	public static final String ORDER_DETAIL_SAVED = "Order detail saved";
	public static final String ORDER_DETAIL_NOT_SAVED = "Order detail not saved";
	public static final String ORDER_DETAIL_ALREADY_EXIST = "Order detail already exists";
	public static final String ORDER_DETAIL_NOT_FOUND = "Order detail not found";
	public static final String PLEASE_INSERT_CORRECT_EMAIL = "Please insert correct email";
	public static final String OTP_SENT_ON_EMAIL = "Otp sent on email";
	public static final String ALL_PRIVILEGES = "All Privileges";

	public static final String ALL_REGISTRATION_TYPE = "All registration type";
	public static final String ALL_WITHDRAWAL = "All withdrawal";
	public static final String WITHDRAWAL_NOT_FOUND = "Withdrawal not found";
	public static final String WITHDRAWAL_ADDED = "Withdrawal Added";
	public static final String WITHDRAWAL_NOT_ADDED = "Withdrawal not Added";

	public static final String VENDOR_ACCOUNT_NUMBER_EXISTS = "Vendor Account Number Already exists";
	public static final String VENDOR_UPI_EXISTS = "Vendor Upi Already exists";
	public static final String VENDOR_CREATED = "Vendor Created";
	public static final String VENDOR_NOT_CREATED = "Vendor not created";
	public static final String NO_VENDORS_FOUND = "No vendors found";
	public static final String NO_VENDOR_RECORDS_FOUND = "No Vendor Records Found";
	public static final String NO_TRANSACTIONS_FOUND = "No transactions found";
	public static final String NO_TRANSACTION_RECORDS_FOUND = "No Transaction Records Found";
	public static final String PLEASE_PASS_ID = "Please Pass id no.";
	public static final String DUPLICATE_MOBILE_NOT_ALLOWED = "Duplicate mobile no not allowed";

	public static final String ALL_SETTLEMENT_DETAILS = "All Settlement Details";
	public static final String ALL_VENDOR_DETAILS = "All Vendor Details";

	public static final String VENDOR_EMAIL_NOT_EXISTS = "Vendor Email not exists";
	public static final String VENDOR_EMAIL_ALREADY_EXISTS = "Vendor email already exists";
	public static final String VENDOR_MOBILE_NOT_EXISTS = "Vendor Mobile not exists";
	public static final String VENDOR_MOBILE_ALREADY_EXISTS = "Vendor Mobile already exists";
	public static final int CONFLICT_STATUS_CODE = 309;
	public static final String CONFLICT_STATUS_MESSAGE = "Conflict: The service already exists.";

	public static final String PAYIN = "payin";
	public static final String PAYOUT = "payout";
	public static final String WITHDRAW = "withdraw";
	public static final String SETTLEMENT = "settlement";
	public static final String CHARGEBACK = "chargeback";
	public static final String RECONCILIATION = "reconciliation";
	public static final String WALLET = "wallet";
	public static final String RELEASE = "release";
	public static final String CREDIT_BALANCE = "credit balance";
	public static final String VENDOR = "vendor";

	public static final String[] WALLET_COLUMN_NAMES = { "amount", "isActive", "remark", "status", "mid",
			"actualAmount", "transactionDate", "utr", "transferMode", "serviceCharge", "gstCharge", "requestFor" };

	public static final String[] RECONCILATION_COLUMN_NAMES = { "date", "transactionId", "particulars", "rrn",
			"deposits", "switchRrn", "amount", "extId", "transactionStatus", "npciCode", "switchCode", "switchMsg",
			"payerVpa", "mid", "payinStatus", "primeryKey", "txnId", "remark", "consolidatedStatus" };
	public static final String[] SETTLEMENT_COLUMN_NAMES = { "mid", // Maps to String mid
			"netAmount", // Maps to BigDecimal netAmount
			"settleToWallet",
"deposoitAmount",
"holdAmount",
			"orderNo", // Maps to String orderNo
			"txnId", // Maps to String txnId
			"vpa", // Maps to String vpa
			"gst", // Maps to BigDecimal gst
			"serviceChanrge", // Maps to BigDecimal serviceCharge (correct spelling)
			"serviceChargeAmount", // Maps to BigDecimal serviceChargeAmount
			"gstAmount", // Maps to BigDecimal gstAmount
			"remark", // Maps to String remark
			"payinAmount", // Maps to BigDecimal payinAmount
			"txnStatus", // Maps to String txnStatus
			"respCode", // Maps to String respCode
			"collectionMethod", // Maps to String collectionMethod
			"terminalId", // Maps to String terminalId
			"createDateTime"// Maps to String settlementDate (corrected spelling)
	};
}

