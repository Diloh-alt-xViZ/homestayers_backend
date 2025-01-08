package com.developer.homestayersbackend.service.impl;

import com.developer.homestayersbackend.dto.*;
import com.developer.homestayersbackend.entity.*;
import com.developer.homestayersbackend.exception.*;
import com.developer.homestayersbackend.repository.*;
import com.developer.homestayersbackend.service.api.BookingService;
import com.developer.homestayersbackend.service.api.SmsService;
import com.developer.homestayersbackend.service.api.TwilioService;
import com.developer.homestayersbackend.util.ListingType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final TwilioService twilioService;
    private final UserRepository userRepository;
    private final PropertyRepository propertyRepository;
    private final HostRepository hostRepository;
    private final RoomRepository roomRepository;
    private final UserProfileRepository userProfileRepository;
    private final SmsService smsService;


    @Override
    public List<BookingStatusDto> getBookingStatuses() {
        return sanitizeBookingStatus(BookingStatus.values());
    }

    private List<BookingStatusDto> sanitizeBookingStatus (BookingStatus[] bookingStatuses){
            List<BookingStatusDto> bookingStatusDtos = new ArrayList<>();
            for (BookingStatus bookingStatus : bookingStatuses) {
                BookingStatusDto bookingStatusDto = new BookingStatusDto();
                String decodedBookingStatus = switch (bookingStatus) {
                    case ACTIVE -> "Active";
                    case PENDING -> "Pending";
                    case CANCELLED -> "Cancelled";
                    case APPROVED -> "Approved";
                    case REJECTED -> "Rejected";
                };
                bookingStatusDto.setStatus(decodedBookingStatus);
                bookingStatusDtos.add(bookingStatusDto);
            }

            return bookingStatusDtos;


    }

    private static String getRejectMessage(Booking booking, String format) {
        Room room = booking.getRoom();
        String message = "";
        if (room != null) {
            message = String.format(format, booking.getGuest().getUsername(), room.getRoomTitle(), booking.getStartDate(), booking.getEndDate());
        } else {
            message = String.format(format, booking.getGuest().getUsername(), booking.getProperty().getTitle(), booking.getStartDate(), booking.getEndDate());
        }
        return message;
    }

    @Override
    public boolean approveBooking(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(()->new BookingNotFoundException("Booking not found"));
        User bookingGuest = booking.getGuest();
        String approvalMessage = String.format("Booking request for %s at %s from %s to %s was approved.",bookingGuest.getUsername(),booking.getProperty().getTitle(),booking.getStartDate(),booking.getEndDate());

        smsService.sendSms(bookingGuest.getPhoneNumber().getFullNumber(),approvalMessage);
            booking.setBookingStatus(BookingStatus.APPROVED);
            booking.setDateUpdated(new Date(System.currentTimeMillis()));
            bookingRepository.save(booking);
        return true;
    }

    @Override
    public boolean denyBookingRequest(BookingDenialRequest request) {
        Booking booking = bookingRepository.findById(request.getBookingId()).orElseThrow(()->new BookingNotFoundException("Booking not found"));
        User bookingGuest = booking.getGuest();
        String denialMessage  = String.format("Booking request for %s at %s was denied.\n %s", bookingGuest.getUsername(),booking.getProperty().getTitle(),request.getReason());

        smsService.sendSms(bookingGuest.getPhoneNumber().getFullNumber(),denialMessage);
        booking.setBookingStatus(BookingStatus.REJECTED);
        booking.setDateUpdated(new Date(System.currentTimeMillis()));
        bookingRepository.save(booking);

        return true;
    }

    @Override
    public String acceptBooking(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(BookingNotFoundException::new);
        booking.setBookingStatus(BookingStatus.APPROVED);
        System.out.println("Getting booked Dates");
        var bookedDates = booking.getBookedDates();
        if(booking.getRoom() != null){
            System.out.println("The booking was for a room");
            Room room    = booking.getRoom();
            room.setBookedDates(bookedDates);
            roomRepository.flush();
        }
        Property property = booking.getProperty();
        property.setUpdatedAt(new Date(System.currentTimeMillis()));
        System.out.println("Setting booked dates");
        bookedDates.forEach(System.out::println);
        property.setBookedDates(bookedDates);
        booking.setDateUpdated(new Date(System.currentTimeMillis()));
        propertyRepository.flush();
        String message = getRejectMessage(booking, "Booking request for %s at %s from %s to %s was accepted");
        bookingRepository.save(booking);
        //smsService.sendSms(booking.getProperty().getHost().getUser().getPhoneNumber().getFullNumber(),message);
        smsService.sendSms(booking.getGuest().getPhoneNumber().getFullNumber(),message);
        return "Success";
    }

    @Override
    public String rejectBooking(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(BookingNotFoundException::new);
        booking.setBookingStatus(BookingStatus.REJECTED);
        booking.setDateUpdated(new Date(System.currentTimeMillis()));
        String message = getRejectMessage(booking, "Booking request for %s at %s from %s to %s was rejected");
        bookingRepository.save(booking);
        //smsService.sendSms(booking.getProperty().getHost().getUser().getPhoneNumber().getFullNumber(),message);
        smsService.sendSmsMessage(booking.getGuest().getPhoneNumber().getFullNumber(),message);
        return  "Success";
    }

    @Override
    public List<ReservationDto> getHostBookings(Long hostId) {

        Host host = hostRepository.findById(hostId).orElseThrow(HostNotFoundException::new);
        List<Booking> bookings = bookingRepository.findBookingByHostId(hostId);
        List<ReservationDto> reservations = new ArrayList<>();

        if(bookings!=null){
            /*for(Booking booking:bookings){
                ReservationDto dto = new ReservationDto();
                dto.setBookingId(booking.getId());
                dto.setFromDate(booking.getStartDate().toString());
                if(booking.getEndDate()!=null){
                    dto.setToDate(booking.getEndDate().toString());
                }
                dto.setStatus(booking.getBookingStatus().getName());
                dto.setPropertyName(booking.getProperty().getTitle());
                UserProfile userProfile = userProfileRepository.findUserProfileByUserId(booking.getGuest().getId());
                dto.setGuestName(userProfile.getFirstName());
                dto.setGuestCount(booking.getNumberOfGuests());
                if(userProfile.getPhoto()!=null){
                dto.setGuestAvatar(userProfile.getPhoto().getUrl());
                }*/
            reservations.addAll(
                    bookings.stream()
                            .map(booking -> {
                                ReservationDto dto = new ReservationDto();
                                dto.setBookingId(booking.getId());
                                dto.setFromDate(booking.getStartDate() != null ? booking.getStartDate().toString() : null);
                                dto.setToDate(booking.getEndDate() != null ? booking.getEndDate().toString() : null);
                                dto.setStatus(booking.getBookingStatus() != null ? booking.getBookingStatus().getName() : null);
                                dto.setPropertyName(booking.getProperty() != null ? booking.getProperty().getTitle() : null);

                                User guest = booking.getGuest();
                                UserProfile userProfile = guest != null
                                        ? userProfileRepository.findUserProfileByUserId(guest.getId())
                                        : null;

                                dto.setGuestName(userProfile != null ? userProfile.getFirstName() : null);
                                dto.setGuestAvatar(userProfile != null && userProfile.getPhoto() != null
                                        ? userProfile.getPhoto().getUrl()
                                        : null);
                                dto.setGuestCount(booking.getNumberOfGuests());

                                return dto;
                            })
                            .toList()
            );
            }
        return reservations;
    }

    @Override
    public List<Booking> getBookingsByUser(Long userId) {

        return bookingRepository.findBookingByGuestId(userId);
    }

    @Override
    public List<Booking> getBookingsByStatus(String bookingStatus) {
        return bookingRepository.findBookingsByBookingStatus(getBookingStatus(bookingStatus));
    }

    private BookingStatus getBookingStatus(String bookingStatus) {
        return switch (bookingStatus) {
            case "Active" -> BookingStatus.ACTIVE;
            case "Pending" -> BookingStatus.PENDING;
            case "Cancelled" -> BookingStatus.CANCELLED;
            case "Approved" -> BookingStatus.APPROVED;
            case "Rejected" -> BookingStatus.REJECTED;
            default -> null;
        };
    }

    @Override
    public BookingResponseDto bookRental(RentalBookingRequest request) {
        Property property = propertyRepository.findById(request.getPropertyId()).orElseThrow(PropertyNotFoundException::new);
        User user = userRepository.findById(request.getGuestId()).orElseThrow(UserNotFoundException::new);
        Booking booking = new Booking();
        booking.setGuest(user);
        booking.setHost(property.getHost());
        booking.setStartDate(request.getStartDate());
        booking.setBookedDates();
        booking.setDateBooked(new Date(System.currentTimeMillis()));
        booking.setBookingStatus(BookingStatus.PENDING);
        booking.setDateUpdated(new Date(System.currentTimeMillis()));
        booking.setProperty(property);
        booking.setPrice(BigDecimal.valueOf(request.getPrice()));
        Booking bookingEntity = bookingRepository.save(booking);
        BookingResponseDto response;
        String message = String.format("Booking request from %s for %s from %s ", user.getUsername(),property.getTitle(),booking.getStartDate());
        try{
            twilioService.sendBookingNotification(property.getHost().getUser().getPhoneNumber(),message,booking.getGuest().getPhoneNumber());
        }
        catch (Exception ex){
            System.out.println(ex.getMessage());
        }
        response= BookingResponseDto.builder().bookingId(bookingEntity.getId()).propertyName(bookingEntity.getProperty().getTitle()).build();
        return response;
    }

    @Override
    public BookingResponseDto bookRoom(BookingRequest request) {

        Property property  = propertyRepository.findById(request.getPropertyId()).orElseThrow(PropertyNotFoundException::new);

        Booking booking = new Booking();
        if(property.getListingType()!= ListingType.RENTAL && property.getListingType()!= ListingType.HOME_STAYERS_EXPERIENCE){
            Room room = roomRepository.findById(request.getRoomId()).orElseThrow(RoomNotFoundException::new);
            booking.setRoom(room);
        }
        User user = userRepository.findById(request.getGuestId()).orElseThrow(UserNotFoundException::new);
        if(user.getVerificationStatus()!=VerificationStatus.VERIFIED){
            throw new UserNotVerifiedException();
        }
        if(user.getPhoneNumber()==null){
            throw new PhoneNumberNotVerifiedException();
        }
        booking.setGuest(user);
        booking.setProperty(property);
        booking.setHost(property.getHost());
        booking.setStartDate(request.getStartDate());
        if(request.getEndDate()!=null){
            booking.setEndDate(request.getEndDate());
        }
        booking.setBookedDates();
        booking.setDateUpdated(new Date(System.currentTimeMillis()));
        booking.setDateBooked(new Date(System.currentTimeMillis()));
        booking.setBookingStatus(BookingStatus.PENDING);
        booking.setNumberOfGuests(request.getNumberOfGuests());
        booking.setPrice(BigDecimal.valueOf(request.getPrice()));
        Booking bookingEntity = bookingRepository.save(booking);
        String message = String.format("Booking request by %s at %s for %s guests from %s to %s", bookingEntity.getGuest().getUsername(),booking.getRoom().getRoomTitle(),booking.getNumberOfGuests(),booking.getStartDate(),booking.getEndDate());
        BookingResponseDto bookingResponseDto;

        try{
            twilioService.sendBookingNotification(property.getHost().getUser().getPhoneNumber(),message,booking.getGuest().getPhoneNumber());
        }
        catch(Exception exception){
            System.out.println(exception.getMessage());
        }
        finally {
            bookingResponseDto= BookingResponseDto.builder().bookingId(bookingEntity.getId()).propertyName(bookingEntity.getProperty().getTitle()).build();
        }
        return bookingResponseDto;
    }

    @Override
    public Booking createBooking(BookingRequest booking) throws Exception {

        Room room = roomRepository.findById(booking.getRoomId()).orElseThrow(()->new RoomNotFoundException("Room not found"));


        Property property = propertyRepository.findById(booking.getPropertyId()).orElseThrow(PropertyNotFoundException::new);
        User guest = userRepository.findById(booking.getGuestId()).orElseThrow(UserNotFoundException::new);


        if(guest.getPhoneNumber()==null){
            throw new Exception("User phone is not verified");
        }
        List<Booking> bookings = bookingRepository.findByPropertyAndStartDateLessThanEqualAndEndDateGreaterThanEqual(property,booking.getEndDate(),booking.getStartDate());

        if(!bookings.isEmpty()){
            throw new Exception("Property is not available for the selected dates");
        }

        Booking bookingEntity = new Booking();
        bookingEntity.setGuest(guest);
        bookingEntity.setStartDate(booking.getStartDate());
        bookingEntity.setEndDate(booking.getEndDate());
        bookingEntity.setProperty(property);
        bookingEntity.setDateBooked(new Date(System.currentTimeMillis()));
        bookingEntity.setBookingStatus(BookingStatus.PENDING);
        //bookingEntity.setPrice(booking.getTotalPrice());
        bookingEntity.setHost(property.getHost());
        bookingEntity.setDateUpdated(new Date(System.currentTimeMillis()));
        Booking savedBooking  = bookingRepository.save(bookingEntity);
//        String message = String.format("Booking request for %s at %s from %s to %s", guest.getUsername(),property.getTitle(),booking.getStartDate(),booking.getEndDate());
  //      System.out.println();
    //    twilioService.sendBookingNotification(property.getHost().getUser().getPhoneNumber(),message,guest.getPhoneNumber());




        return savedBooking;
    }
}
