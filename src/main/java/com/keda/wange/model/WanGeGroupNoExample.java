package com.keda.wange.model;

import java.util.ArrayList;
import java.util.List;

public class WanGeGroupNoExample {
    protected String orderByClause;

    protected boolean distinct;

    protected List<Criteria> oredCriteria;

    public WanGeGroupNoExample() {
        oredCriteria = new ArrayList<Criteria>();
    }

    public void setOrderByClause(String orderByClause) {
        this.orderByClause = orderByClause;
    }

    public String getOrderByClause() {
        return orderByClause;
    }

    public void setDistinct(boolean distinct) {
        this.distinct = distinct;
    }

    public boolean isDistinct() {
        return distinct;
    }

    public List<Criteria> getOredCriteria() {
        return oredCriteria;
    }

    public void or(Criteria criteria) {
        oredCriteria.add(criteria);
    }

    public Criteria or() {
        Criteria criteria = createCriteriaInternal();
        oredCriteria.add(criteria);
        return criteria;
    }

    public Criteria createCriteria() {
        Criteria criteria = createCriteriaInternal();
        if (oredCriteria.size() == 0) {
            oredCriteria.add(criteria);
        }
        return criteria;
    }

    protected Criteria createCriteriaInternal() {
        Criteria criteria = new Criteria();
        return criteria;
    }

    public void clear() {
        oredCriteria.clear();
        orderByClause = null;
        distinct = false;
    }

    protected abstract static class GeneratedCriteria {
        protected List<Criterion> criteria;

        protected GeneratedCriteria() {
            super();
            criteria = new ArrayList<Criterion>();
        }

        public boolean isValid() {
            return criteria.size() > 0;
        }

        public List<Criterion> getAllCriteria() {
            return criteria;
        }

        public List<Criterion> getCriteria() {
            return criteria;
        }

        protected void addCriterion(String condition) {
            if (condition == null) {
                throw new RuntimeException("Value for condition cannot be null");
            }
            criteria.add(new Criterion(condition));
        }

        protected void addCriterion(String condition, Object value, String property) {
            if (value == null) {
                throw new RuntimeException("Value for " + property + " cannot be null");
            }
            criteria.add(new Criterion(condition, value));
        }

        protected void addCriterion(String condition, Object value1, Object value2, String property) {
            if (value1 == null || value2 == null) {
                throw new RuntimeException("Between values for " + property + " cannot be null");
            }
            criteria.add(new Criterion(condition, value1, value2));
        }

        public Criteria andWangeGroupNoIsNull() {
            addCriterion("WANGE_GROUP_NO is null");
            return (Criteria) this;
        }

        public Criteria andWangeGroupNoIsNotNull() {
            addCriterion("WANGE_GROUP_NO is not null");
            return (Criteria) this;
        }

        public Criteria andWangeGroupNoEqualTo(String value) {
            addCriterion("WANGE_GROUP_NO =", value, "wangeGroupNo");
            return (Criteria) this;
        }

        public Criteria andWangeGroupNoNotEqualTo(String value) {
            addCriterion("WANGE_GROUP_NO <>", value, "wangeGroupNo");
            return (Criteria) this;
        }

        public Criteria andWangeGroupNoGreaterThan(String value) {
            addCriterion("WANGE_GROUP_NO >", value, "wangeGroupNo");
            return (Criteria) this;
        }

        public Criteria andWangeGroupNoGreaterThanOrEqualTo(String value) {
            addCriterion("WANGE_GROUP_NO >=", value, "wangeGroupNo");
            return (Criteria) this;
        }

        public Criteria andWangeGroupNoLessThan(String value) {
            addCriterion("WANGE_GROUP_NO <", value, "wangeGroupNo");
            return (Criteria) this;
        }

        public Criteria andWangeGroupNoLessThanOrEqualTo(String value) {
            addCriterion("WANGE_GROUP_NO <=", value, "wangeGroupNo");
            return (Criteria) this;
        }

        public Criteria andWangeGroupNoLike(String value) {
            addCriterion("WANGE_GROUP_NO like", value, "wangeGroupNo");
            return (Criteria) this;
        }

        public Criteria andWangeGroupNoNotLike(String value) {
            addCriterion("WANGE_GROUP_NO not like", value, "wangeGroupNo");
            return (Criteria) this;
        }

        public Criteria andWangeGroupNoIn(List<String> values) {
            addCriterion("WANGE_GROUP_NO in", values, "wangeGroupNo");
            return (Criteria) this;
        }

        public Criteria andWangeGroupNoNotIn(List<String> values) {
            addCriterion("WANGE_GROUP_NO not in", values, "wangeGroupNo");
            return (Criteria) this;
        }

        public Criteria andWangeGroupNoBetween(String value1, String value2) {
            addCriterion("WANGE_GROUP_NO between", value1, value2, "wangeGroupNo");
            return (Criteria) this;
        }

        public Criteria andWangeGroupNoNotBetween(String value1, String value2) {
            addCriterion("WANGE_GROUP_NO not between", value1, value2, "wangeGroupNo");
            return (Criteria) this;
        }

        public Criteria andCvsMeetingIdIsNull() {
            addCriterion("CVS_MEETING_ID is null");
            return (Criteria) this;
        }

        public Criteria andCvsMeetingIdIsNotNull() {
            addCriterion("CVS_MEETING_ID is not null");
            return (Criteria) this;
        }

        public Criteria andCvsMeetingIdEqualTo(String value) {
            addCriterion("CVS_MEETING_ID =", value, "cvsMeetingId");
            return (Criteria) this;
        }

        public Criteria andCvsMeetingIdNotEqualTo(String value) {
            addCriterion("CVS_MEETING_ID <>", value, "cvsMeetingId");
            return (Criteria) this;
        }

        public Criteria andCvsMeetingIdGreaterThan(String value) {
            addCriterion("CVS_MEETING_ID >", value, "cvsMeetingId");
            return (Criteria) this;
        }

        public Criteria andCvsMeetingIdGreaterThanOrEqualTo(String value) {
            addCriterion("CVS_MEETING_ID >=", value, "cvsMeetingId");
            return (Criteria) this;
        }

        public Criteria andCvsMeetingIdLessThan(String value) {
            addCriterion("CVS_MEETING_ID <", value, "cvsMeetingId");
            return (Criteria) this;
        }

        public Criteria andCvsMeetingIdLessThanOrEqualTo(String value) {
            addCriterion("CVS_MEETING_ID <=", value, "cvsMeetingId");
            return (Criteria) this;
        }

        public Criteria andCvsMeetingIdLike(String value) {
            addCriterion("CVS_MEETING_ID like", value, "cvsMeetingId");
            return (Criteria) this;
        }

        public Criteria andCvsMeetingIdNotLike(String value) {
            addCriterion("CVS_MEETING_ID not like", value, "cvsMeetingId");
            return (Criteria) this;
        }

        public Criteria andCvsMeetingIdIn(List<String> values) {
            addCriterion("CVS_MEETING_ID in", values, "cvsMeetingId");
            return (Criteria) this;
        }

        public Criteria andCvsMeetingIdNotIn(List<String> values) {
            addCriterion("CVS_MEETING_ID not in", values, "cvsMeetingId");
            return (Criteria) this;
        }

        public Criteria andCvsMeetingIdBetween(String value1, String value2) {
            addCriterion("CVS_MEETING_ID between", value1, value2, "cvsMeetingId");
            return (Criteria) this;
        }

        public Criteria andCvsMeetingIdNotBetween(String value1, String value2) {
            addCriterion("CVS_MEETING_ID not between", value1, value2, "cvsMeetingId");
            return (Criteria) this;
        }

        public Criteria andWangeFromDevicesIsNull() {
            addCriterion("WANGE_FROM_DEVICES is null");
            return (Criteria) this;
        }

        public Criteria andWangeFromDevicesIsNotNull() {
            addCriterion("WANGE_FROM_DEVICES is not null");
            return (Criteria) this;
        }

        public Criteria andWangeFromDevicesEqualTo(String value) {
            addCriterion("WANGE_FROM_DEVICES =", value, "wangeFromDevices");
            return (Criteria) this;
        }

        public Criteria andWangeFromDevicesNotEqualTo(String value) {
            addCriterion("WANGE_FROM_DEVICES <>", value, "wangeFromDevices");
            return (Criteria) this;
        }

        public Criteria andWangeFromDevicesGreaterThan(String value) {
            addCriterion("WANGE_FROM_DEVICES >", value, "wangeFromDevices");
            return (Criteria) this;
        }

        public Criteria andWangeFromDevicesGreaterThanOrEqualTo(String value) {
            addCriterion("WANGE_FROM_DEVICES >=", value, "wangeFromDevices");
            return (Criteria) this;
        }

        public Criteria andWangeFromDevicesLessThan(String value) {
            addCriterion("WANGE_FROM_DEVICES <", value, "wangeFromDevices");
            return (Criteria) this;
        }

        public Criteria andWangeFromDevicesLessThanOrEqualTo(String value) {
            addCriterion("WANGE_FROM_DEVICES <=", value, "wangeFromDevices");
            return (Criteria) this;
        }

        public Criteria andWangeFromDevicesLike(String value) {
            addCriterion("WANGE_FROM_DEVICES like", value, "wangeFromDevices");
            return (Criteria) this;
        }

        public Criteria andWangeFromDevicesNotLike(String value) {
            addCriterion("WANGE_FROM_DEVICES not like", value, "wangeFromDevices");
            return (Criteria) this;
        }

        public Criteria andWangeFromDevicesIn(List<String> values) {
            addCriterion("WANGE_FROM_DEVICES in", values, "wangeFromDevices");
            return (Criteria) this;
        }

        public Criteria andWangeFromDevicesNotIn(List<String> values) {
            addCriterion("WANGE_FROM_DEVICES not in", values, "wangeFromDevices");
            return (Criteria) this;
        }

        public Criteria andWangeFromDevicesBetween(String value1, String value2) {
            addCriterion("WANGE_FROM_DEVICES between", value1, value2, "wangeFromDevices");
            return (Criteria) this;
        }

        public Criteria andWangeFromDevicesNotBetween(String value1, String value2) {
            addCriterion("WANGE_FROM_DEVICES not between", value1, value2, "wangeFromDevices");
            return (Criteria) this;
        }
    }

    public static class Criteria extends GeneratedCriteria {

        protected Criteria() {
            super();
        }
    }

    public static class Criterion {
        private String condition;

        private Object value;

        private Object secondValue;

        private boolean noValue;

        private boolean singleValue;

        private boolean betweenValue;

        private boolean listValue;

        private String typeHandler;

        public String getCondition() {
            return condition;
        }

        public Object getValue() {
            return value;
        }

        public Object getSecondValue() {
            return secondValue;
        }

        public boolean isNoValue() {
            return noValue;
        }

        public boolean isSingleValue() {
            return singleValue;
        }

        public boolean isBetweenValue() {
            return betweenValue;
        }

        public boolean isListValue() {
            return listValue;
        }

        public String getTypeHandler() {
            return typeHandler;
        }

        protected Criterion(String condition) {
            super();
            this.condition = condition;
            this.typeHandler = null;
            this.noValue = true;
        }

        protected Criterion(String condition, Object value, String typeHandler) {
            super();
            this.condition = condition;
            this.value = value;
            this.typeHandler = typeHandler;
            if (value instanceof List<?>) {
                this.listValue = true;
            } else {
                this.singleValue = true;
            }
        }

        protected Criterion(String condition, Object value) {
            this(condition, value, null);
        }

        protected Criterion(String condition, Object value, Object secondValue, String typeHandler) {
            super();
            this.condition = condition;
            this.value = value;
            this.secondValue = secondValue;
            this.typeHandler = typeHandler;
            this.betweenValue = true;
        }

        protected Criterion(String condition, Object value, Object secondValue) {
            this(condition, value, secondValue, null);
        }
    }
}