package stayops_api.dto;

public class RoomTypesRequest {
    private String roomTypesJson;
    private int totalRoomsCount;

    public RoomTypesRequest() {}

    public String getRoomTypesJson() {
        return roomTypesJson;
    }

    public void setRoomTypesJson(String roomTypesJson) {
        this.roomTypesJson = roomTypesJson;
    }

    public int getTotalRoomsCount() {
        return totalRoomsCount;
    }

    public void setTotalRoomsCount(int totalRoomsCount) {
        this.totalRoomsCount = totalRoomsCount;
    }
}
