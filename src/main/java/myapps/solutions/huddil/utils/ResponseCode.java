package myapps.solutions.huddil.utils;

public interface ResponseCode {

	String invalidSessionId = "9999";
	String invalidUserType = "9998";
	String invalidData = "9997";
	String accessRestricted = "9996";
	String EnquiryMailSend = "9995";

	String FacilityTypeAddSuccessful = "2001";
	String FacilityTypeAddFailure = "2002";
	String FacilityTypeAlreadyAdded = "2003";

	String FacilityTypeReadSuccessful = "2011";
	String FacilityTypeReadFailure = "2012";

	String MultitenantAddSuccessful = "2021";
	String MultitenantAddFailure = "2022";
	String MultitenantReadSuccessful = "2031";
	String MultitenantReadFailure = "2032";
	String MultitenantUpdateSuccessful = "2041";
	String MultitenantUpdateFailure = "2042";
	String MultitenantDeleteSuccessful = "2051";
	String MultitenantDeleteFailure = "2052";

	String BankDetailsAddSuccessful = "2061";
	String BankDetailsAddFailure = "2062";
	String BankDetailsReadSuccessful = "2071";
	String BankDetailsReadFailure = "2072";
	String BankDetailsDeleteSuccessful = "2081";
	String BankDetailsDeleteFailure = "2082";

	String BookingCreateSuccessful = "2091";
	String BookingCreateFailure = "2092";
	String BookingReadSuccessful = "2101";
	String BookingReadFailure = "2102";
	String BookingCancelReadSuccessful = "2103";
	String BookingCancelReadFailure = "2104";

	String FacilityAddSuccessful = "2111";
	String FacilityAddFailure = "2112";
	String FacilityReadSuccessful = "2121";
	String FacilityReadFailure = "2122";
	String FacilityDeleteSuccessful = "2131";
	String FacilityDeleteFailure = "2132";
	String FacilityDeleteNotSavedState = "2133";
	String FacilityDeleteNotOwner = "2134";

	String OfferAddSuccessful = "2131";
	String OfferAddFailure = "2132";

	String FacilityOfferReadSuccess = "2141";
	String FacilityOfferReadFailure = "2142";

	String OfferAlreadyExistForTheGivenPeriod = "2143";
	String FacilityIsBlocked = "2144";

	String OfferDeleteSuccessful = "2151";
	String OfferDeleteFailure = "2152";

	String FavoritiesAddSuccessful = "2181";
	String FavoritiesAddFailure = "2182";
	String FavoritiesAlreadyAdded = "2183";

	String FavoritiesReadSuccess = "2191";
	String FavoritiesReadFailure = "2192";

	String FavoritiesDeleteSuccess = "2201";
	String FavoritiesDeleteFailure = "2202";

	String ReviewAddSuccesssful = "2211";
	String ReviewAddFailure = "2212";

	String CityAddSuccess = "2221";
	String CityAddFailure = "2222";
	String CityAlreadyAdded = "2223";

	String ReadCitySuccessful = "2231";
	String ReadCityFailure = "2232";

	String UpdateCitySuccessful = "2241";
	String UpdateCityFailure = "2242";

	String DeleteCitySuccessful = "2251";
	String DeleteCityFailure = "2252";

	String ReviewReadSuccesssful = "2261";
	String ReviewReadFailure = "2262";

	String AmenityAddSuccess = "2271";
	String AmenityAddFailure = "2272";
	String AmenityAlreadyExist = "2273";

	String AmenityReadSuccess = "2281";
	String AmenityReadFailure = "2282";

	String AmenityDeleteSuccess = "2291";
	String AmenityDeleteFailure = "2292";

	String MaintenanceAddSuccessful = "2301";
	String MaintenanceAddFailure = "2302";

	String MaintenanceReadSuccessful = "2311";
	String MaintenanceReadFailure = "2312";

	String MaintenanceUpdateSuccessful = "2321";
	String MaintenanceUpdateFailure = "2322";

	String MaintenanceDeleteSuccessful = "2331";
	String MaintenanceDeleteFailure = "2332";

	String UpdateFacilityStatusSuccessful = "2341";
	String UpdateFacilityStatusFailure = "2342";
	String ServiceProviderIsBlocked = "2343";

	String UserTypeUpdateSuccessful = "2351";
	String UserTypeUpdateFailure = "2352";

	String AdditionalCostAddedSuccessfully = "2361";
	String AdditionalCostAddedFailure = "2362";

	String AdditionalCostReadSuccessfully = "2371";
	String AdditionalCostReadFailure = "2372";

	String LocalityAddSuccessful = "2381";
	String LocalityAddFailure = "2382";
	String LocalityAlreadyExist = "2383";

	String ReadLocalitySuccessful = "2391";
	String ReadLocalityFailure = "2392";

	String UpdateLocalitySuccessful = "2401";
	String UpdateLocalityFailure = "2402";

	String AddLocationSuccessful = "2411";
	String AddLocationFailure = "2412";

	String ReadLocationSuccessful = "2421";
	String ReadLocationFailure = "2422";

	String UpdateLocationSuccessful = "2431";
	String UpdateLocationFailure = "2432";

	String DeleteLocationSuccessful = "2441";
	String DeleteLocationFailure = "2442";

	String ReviewDeleteSuccessful = "2451";
	String ReviewDeleteFailure = "2452";

	String ReadFacilityStatusCountSuccessful = "2461";
	String ReadFacilityStatusCountFailure = "2462";

	String FileUploadSuccessful = "2471";
	String FileUploadFailure = "2472";
	String FileReadSuccessful = "2473";
	String FileReadFailure = "2474";

	String FacilityUpdateSuccessful = "2481";
	String FacilityUpdateFailure = "2482";

	String FacilityPriceUpdationSuccessful = "2483";
	String FacilityPriceUpdationFailure = "2484";
	String FacilityNotFound = "2485";
	String FacilityStatusCannotBeChanged = "2486";

	String ReadEventSuccessful = "2501";
	String ReadEventFailure = "2502";
	String UpdateEventSuccessful = "2503";
	String UpdateEventFailure = "2504";

	String BlockFacilitySuccessful = "2511";
	String BlockFacilityFailure = "2512";
	String BookingExist = "2513";
	String BookingDoesNotExist = "2514";

	String ReadUserSuccessful = "2601";
	String ReadUserFailure = "2602";
	String UpdateUserSuccessful = "2603";
	String UpdateUserFailure = "2604";

	String UpdateUserNotInActive = "2605";
	String UpdateUserNotInDeactive = "2606";

	String PaymentReadSuccess = "2611";
	String PaymentReadFailure = "2612";

	String ReadStatusSuccessful = "2621";
	String ReadStatusFailure = "2622";

	String BookingStatusUpdateSuccessful = "2631";
	String BookingStatusUpdateFailure = "2632";
	String NoEnoughSeatsAvailable = "2633";
	String AlreadyUpdated = "2634";
	String RefundAmountCalculatedSucessfully = "2635";

	String HuddilVerifyRequestSuccessful = "2641";
	String HuddilVerifyRequestFailure = "2642";

	String FacilityHistoryReadSuccessful = "2651";
	String FacilityHistoryReadFailure = "2652";

	String ParticipantsTeamAddSuccessful = "2711";
	String ParticipantsTeamAddFailure = "2712";

	String ParticipantsTeamReadSuccessful = "2721";
	String ParticipantsTeamReadFailure = "2722";

	String ParticipantsTeamUpdateSuccessful = "2731";
	String ParticipantsTeamUpdateFailure = "2732";

	String ParticipantsTeamDeleteSuccessful = "2741";
	String ParticipantsTeamDeleteFailure = "2742";

	String AddParticipantSuccessful = "2811";
	String AddParticipantsFailure = "2812";
	String AddParticipantsPartiallySuccessful = "2813";
	String AddParticipantsDenied = "2814";

	String ReadParticipantsSuccessful = "2821";
	String ReadParticipantsFailure = "2822";

	String UpdateParticipantsSuccessful = "2831";
	String UpdateParticipantsFailure = "2832";

	String DeleteParticipantsSuccessful = "2841";
	String DeleteParticipantsFailure = "2842";

	String AddMeetingSuccessful = "2911";
	String AddMeetingFailure = "2912";
	String MeetingAlreadAdded = "2913";

	String MeetingReadSuccessful = "2921";
	String MeetingReadFailure = "2922";

	String UpdateMeetingSuccessful = "2931";
	String UpdateMeetingFailure = "2932";

	String DeleteMeetingSuccessful = "2941";
	String DeleteMeetingFailure = "2942";

	String CreateBookingFromTimeAfterToTime = "3000";
	String CreateBookingInvalidFacilityId = "3003";
	String CreateBookingFacilityNotAvailable = "3004";
	String CreateBookingFacilityUnderMaintenance = "3005";
	String CreateBookingFromBeforeOpening = "3006";
	String CreateBookingEndAfterClosing = "3007";
	String CreateBookingCoWorkNotEnoughSeats = "3008";
	String CreateBookingCoWorkSeats = "3009";
	String CreateBookingExists = "3010";
	String CreateBookingAvailable = "3011";
	String CreateBookingInvalidBookingId = "3012";
	String CreateBookingInvalidUserId = "3013";
	String CreateBookingConfirmed = "3014";
	String CreateBookingDupPaymentId = "3015";
	String CreateBookingCreated = "3016";
	String CreateBookingInvalidPaymentId = "3017";
	String CreateBookingFacilityClosed = "3018";
	String CreateBookingInvalidTime = "3019";
	String CreateBookingInvalidCapacity = "3020";
	String CreateBookingFacilityPriceChanged = "3021";

	String CancelBookingInvalidBookingId = "3103";
	String CancelBookingInvalidType = "3105";
	String CancelBookingPaymentInProgress = "3106";
	String CancelBookingMeetingInProgress = "3107";
	String CancelBookingInvalidStatus = "3108";
	String CancelBookingNotAuth = "3109";
	String CancelBookingOffline = "3110";
	String CancelBookingOfflineCancelled = "3111";
	String CancelBookingOnline = "3112";
	String CancelBookingOnlineCancelled = "3113";
	String CancelBookingNotOwner = "3114";
	String CancelBookingFailed = "3100";

	String CancellationReadSuccessful = "3201";
	String CancellationReadFailure = "3202";

	String AdminDBUserReadSuccessful = "4001";
	String AdminDBFacilityReadSuccessful = "4002";
	String AdminDBPaymentReadSuccessful = "4003";

	String CommissionUpdatedSuccessfully = "4011";
	String CommissionUpdationFailure = "4012";
	String SameCommissionExistForMonth = "4013";

	String CommissionReadSuccessfull = "4014";
	String CommissionReadFailure = "4015";

	String FacilityTermsConditionAddSuccessful = "4016";
	String FacilityTermsConditionAddFailure = "4017";
	
}
