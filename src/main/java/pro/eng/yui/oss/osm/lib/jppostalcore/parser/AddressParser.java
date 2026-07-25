package pro.eng.yui.oss.osm.lib.jppostalcore.parser;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AddressParser {
    
    public static class JpAddress {
        public enum Avail{
            /** 許容値 */
            YES, 
            /** 不適合値 */
            NO, 
            /** 未設定 */
            UNSET;
        }

        private Avail isPostcodeAvail = Avail.UNSET;
        public Avail isPostcodeAvail(){ return isPostcodeAvail; }
        private String postcode;
        public void setPostcode(String value){
            this.postcode = value; this.isPostcodeAvail = isPostcodeAvail(value);
        }
        public String getPostcode(){ return postcode; }
        
        private Avail isProvinceAvail = Avail.UNSET;
        public Avail isProvinceAvail(){ return isProvinceAvail; }
        private String province;
        public void setProvince(String value){
            this.province = value; this.isProvinceAvail = isProvinceAvail(value);
        }
        public String getProvince(){ return province; }
        
        private Avail isCountyAvail = Avail.UNSET;
        public Avail isCountyAvail(){ return isCountyAvail; }
        private String county;
        public void setCounty(String value){
            this.county = value; this.isCountyAvail = isCountyAvail(value);
        }
        public String getCounty(){ return county; }
        
        private Avail isCityAvail = Avail.UNSET;
        public Avail isCityAvail(){ return isCityAvail; }
        private String city;
        public void setCity(String value){
            this.city = value; this.isCityAvail = isCityAvail(value);
        }
        public String getCity(){ return city; }

        private Avail isSuburbAvail = Avail.UNSET;
        public Avail isSuburbAvail(){ return isSuburbAvail; }
        private String suburb;
        public void setSuburb(String value){
            this.suburb = value; this.isSuburbAvail = isSuburbAvail(value);
        }
        public String getSuburb(){ return suburb; }

        private Avail isQuarterAvail = Avail.UNSET;
        public Avail isQuarterAvail(){ return isQuarterAvail; }
        private String quarter;
        public void setQuarter(String value){
            this.quarter = value; this.isQuarterAvail = isAvail(value);
        }
        public String getQuarter(){ return quarter; }

        private Avail isNeighbourhoodAvail = Avail.UNSET;
        public Avail isNeighbourhoodAvail(){ return isNeighbourhoodAvail; }
        private String neighbourhood;
        public void setNeighbourhood(String value) {
            this.neighbourhood = value; this.isNeighbourhoodAvail = isAvail(value);
        }
        public String getNeighbourhood() {
            return neighbourhood;
        }

        private Avail isBlockNumberAvail = Avail.UNSET;
        public Avail isBlockNumberAvail(){ return isBlockNumberAvail; }
        private String block_number;
        public void setBlockNumber(String value){
            this.block_number = value; this.isBlockNumberAvail = isAvail(value);
        }
        public String getBlockNumber(){ return block_number; }

        private Avail isHousenumberAvail = Avail.UNSET;
        public Avail isHousenumberAvail(){ return isHousenumberAvail; }
        private String housenumber;
        public void setHousenumber(String value){
            this.housenumber = value; this.isHousenumberAvail = isAvail(value);
        }
        public String getHousenumber(){ return housenumber; }

        private Avail isHousenameAvail = Avail.UNSET;
        public Avail isHousenameAvail(){ return isHousenameAvail; }
        private String housename;
        public void setHousename(String value){
            this.housename = value; this.isHousenameAvail = isAvail(value);
        }
        public String getHousename(){ return housename; }

        private Avail isFloorAvail = Avail.UNSET;
        public Avail isFloorAvail(){ return isFloorAvail; }
        private String floor;
        public void setFloor(String value){
            this.floor = value; this.isFloorAvail = isAvailFloor(value);
        }
        public String getFloor(){ return housename; }

        private Avail isRoomAvail = Avail.UNSET;
        public Avail isRoomAvail(){ return isRoomAvail; }
        private String room;
        public void setRoom(String value){
            this.room = value; this.isRoomAvail = isAvail(value);
        }
        public String getRoom(){ return room; }
        
        /** YES or UNSET */
        private Avail isFullAvail = Avail.UNSET;
        public Avail isFullAvail(){ return isFullAvail; }
        private String full;
        public void setFull(String value){
            this.full = value; isFullAvail = isAvail(value);
            this.postcode = null; isPostcodeAvail = Avail.UNSET;
            this.province = null; isProvinceAvail = Avail.UNSET;
            this.county = null; isCountyAvail = Avail.UNSET;
            this.city = null; isCityAvail = Avail.UNSET;
            this.suburb = null; isSuburbAvail = Avail.UNSET;
            this.quarter = null; isQuarterAvail = Avail.UNSET;
            this.neighbourhood = null; isNeighbourhoodAvail = Avail.UNSET;
            this.block_number = null; isBlockNumberAvail = Avail.UNSET;
            this.housenumber = null; isHousenumberAvail = Avail.UNSET;
            this.housename = null; isHousenameAvail = Avail.UNSET;
            this.floor = null; isFloorAvail = Avail.UNSET;
            this.room = null; isRoomAvail = Avail.UNSET;
        }
        
        public JpAddress(){}
        
        public static JpAddress of(Map<String,String> tags) {
            JpAddress result = new JpAddress();
            result.map(tags);
            return result;
        }
        
        @Override
        public String toString(){
            if (isFullAvail != Avail.UNSET) {
                return "full: "+ full;
            }
            
            StringBuilder builder = new StringBuilder();
            if (isPostcodeAvail != Avail.UNSET) {
                builder.append("〒").append(postcode).append(" ");
            }
            if (isProvinceAvail != Avail.UNSET) { builder.append(province); }
            if (isCountyAvail != Avail.UNSET){ builder.append(county); }
            if (isCityAvail != Avail.UNSET) { builder.append(city); }
            if (isSuburbAvail != Avail.UNSET) { builder.append(suburb); }
            if (isQuarterAvail != Avail.UNSET) { builder.append(quarter); }
            if (isNeighbourhoodAvail != Avail.UNSET) { builder.append(neighbourhood); }
            if (isBlockNumberAvail != Avail.UNSET && isHousenumberAvail != Avail.UNSET) {
                builder.append(block_number).append("-").append(housenumber);
            } else {
                if (isBlockNumberAvail != Avail.UNSET) { builder.append(block_number); }
                if (isHousenumberAvail != Avail.UNSET) { builder.append(housenumber); }
            }
            if (isHousenameAvail != Avail.UNSET) { builder.append(" ").append(housename).append(" "); }
            if (isFloorAvail != Avail.UNSET) { 
                builder.append(floor);
                if (!floor.endsWith("階") && !floor.endsWith("F")){ builder.append("階"); }
            }
            if (isRoomAvail != Avail.UNSET) { builder.append(room); }
            return builder.toString().trim();
        }
        
        public void map(Map<String, String> tags){
            String fullAddress = tags.get("addr:full");
            if (fullAddress != null) { 
                setFull(fullAddress);
                return;
            }

            setPostcode(tags.get("addr:postcode"));
            setProvince(tags.get("addr:province"));
            setCounty(tags.get("addr:county"));
            setCity(tags.get("addr:city"));
            setSuburb(tags.get("addr:suburb"));
            setQuarter(tags.get("addr:quarter"));
            setNeighbourhood(tags.get("addr:neighbourhood"));
            setBlockNumber(tags.get("addr:block_number"));
            setHousenumber(tags.get("addr:housenumber"));
            setHousename(tags.get("addr:housename"));
            setFloor(tags.get("addr:floor"));
            setRoom(tags.get("addr:room"));
        }
        /** ヘルパーメソッド 文字列が有効か判定する */
        private boolean isNotEmpty(String value) {
            if (value == null) { return false; }
            for (int i = 0; i < value.length(); i++) {
                char c = value.charAt(i);
                if (!(Character.isWhitespace(c) || c == '　')) {
                    return true;
                }
            }
            return false;
        }
        
        public Avail isPostcodeAvail(String value){
            if (isNotEmpty(value)) {
                Pattern pattern = Pattern.compile("\\d{3}-\\d{4}");
                Matcher matcher = pattern.matcher(value);
                return matcher.matches() ? Avail.YES : Avail.NO;
            }
            return Avail.UNSET;
        }
        
        public Avail isProvinceAvail(String value){
            if (isNotEmpty(value)){
                char lastChar = value.charAt(value.length() - 1);
                if (lastChar == '都' || lastChar == '道' || lastChar == '府' || lastChar == '県') {
                    return Avail.YES;
                }
                return Avail.NO;
            }
            return Avail.UNSET;
        }

        public Avail isCountyAvail(String value){
            if (isNotEmpty(value)){
                char lastChar = value.charAt(value.length() - 1);
                return (lastChar == '郡') ? Avail.YES : Avail.NO;
            }
            return Avail.UNSET;
        }
        
        public Avail isCityAvail(String value){
            if (isNotEmpty(value)){
                char lastChar = value.charAt(value.length() - 1);
                if (lastChar == '市' || lastChar == '区' || lastChar == '町' || lastChar == '村') {
                    return Avail.YES;
                }
                return Avail.NO;
            }
            return Avail.UNSET;
        }

        public Avail isSuburbAvail(String value){
            if (isNotEmpty(value)){
                char lastChar = value.charAt(value.length() - 1);
                return (lastChar == '区') ? Avail.YES : Avail.NO;
            }
            return Avail.UNSET;
        }
        
        public Avail isAvailFloor(String value){
            if (isNotEmpty(value)) {
                // 0.5刻みの数字、マイナス許容、末尾 Fまたは階 の付属許容
                Pattern pattern = Pattern.compile("-?\\d+(?:\\.5)?[F階]?");
                Matcher matcher = pattern.matcher(value);
                return matcher.matches() ? Avail.YES : Avail.NO;
            }
            return Avail.UNSET;
        }
        
        public Avail isAvail(String value){
            if (isNotEmpty(value)){
                return Avail.YES;
            }
            return Avail.UNSET;
        }

    }

}
