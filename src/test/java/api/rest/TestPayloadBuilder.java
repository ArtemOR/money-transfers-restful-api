package api.rest;

import com.google.gson.Gson;

public class TestPayloadBuilder {

   private String name;
   private String passportId;
   private String moneyBalance;
   private String creditLimit;
   private String accountToId;
   private String accountFromId;
   private String amount;

   public TestPayloadBuilder setName(String name) {
      this.name = name;
      return this;
   }

   public TestPayloadBuilder setPassportId(String passportId) {
      this.passportId = passportId;
      return this;
   }

   public TestPayloadBuilder setCreditLimit(String creditLimit) {
      this.creditLimit = creditLimit;
      return this;
   }

   public TestPayloadBuilder setMoneyBalance(String moneyBalance) {
      this.moneyBalance = moneyBalance;
      return this;
   }

   public TestPayloadBuilder setAccountToId(String accountToId) {
      this.accountToId = accountToId;
      return this;
   }

   public TestPayloadBuilder setAccountFromId(String accountFromId) {
      this.accountFromId = accountFromId;
      return this;
   }

   public TestPayloadBuilder setAmount(String amount) {
      this.amount = amount;
      return this;
   }

   public String buildPayload() {
      return  new Gson().toJson(this);
   }
}
