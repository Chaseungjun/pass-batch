package com.example.adapter.message;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Collections;
import java.util.List;

@Getter
@Setter
@ToString
public class KakaoTalkMessageRequest {

    @JsonProperty("template_object")  // JSON 데이터의 key와 매핑하기 위해
    private TemplateObject templateObject;  // 메시지 템플릿 정보

    @JsonProperty("receiver_uuids")
    private List<String> receiverUuids;
    // 메시지를 받을 대상의 UUID, 피커 또는 친구 목록 가져오기 API를 통해 얻은 사용자 uuid 값


    public KakaoTalkMessageRequest(String uuid, String text) {
        /**
         * 인자로 전달된 값으로 단일 요소의 리스트 객체를 만들어 반환한다.
         * 이렇게 생성된 리스트는 변경 불가 리스트로, add() 나 remove() 와 같은 메소드를 사용할 수 없다.
         *
         * 해당 변수가 다른 객체의 필드 값으로 사용되는 경우 등 리스트 객체를 사용하는 다른 메서드나 객체에 전달할 때
         * 새로운 리스트 객체를 생성해야 할 수 있다. 따라서 변수가 하나만 들어가더라도 리스트 객체를 사용기에 적절한 상황이 있다
         */
        List<String> receiverUuids = Collections.singletonList(uuid);

        TemplateObject.Link link = new TemplateObject.Link();

        TemplateObject templateObject = new TemplateObject();
        templateObject.setObjectType("text");
        templateObject.setText(text);
        templateObject.setLink(link);

        this.receiverUuids = receiverUuids;
        this.templateObject = templateObject;

    }

    @Getter
    @Setter
    @ToString
    public static class TemplateObject {
        @JsonProperty("object_type")
        private String objectType;   // 메시지 유형
        private String text;
        private Link link;   //Link에 다양한 정보가 들어갈 수 있으므로 따로 클래스 생성

        @Getter
        @Setter
        @ToString
        public static class Link {
            @JsonProperty("web_url")
            private String webUrl;    // 메시지와 연결된 웹 url

        }

    }
}
