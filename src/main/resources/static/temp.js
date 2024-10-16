"use strict";

class Duration {

 #seconds;

 constructor(seconds) {
   this.#seconds = seconds;
 }

 /**
  * A static factory method which instantiates a new Duration object.
  * 
  * @param {number} value a float which specifies the value of this duration
  * @param {DurationUnit} durationUnit defines the time unit of this duration value
  * @returns {Duration} a Duration object with specified values
  */
 static of(value, durationUnit) {
   let seconds = value * durationUnit.multiplier;
   return new Duration(seconds);
 }

 /**
  * Converts the value of the duration to the specified DurationUnit.
  * 
  * @param {DurationUnit} durationUnit 
  * @param {number} decimalNum the number of decimal spaces to round to (no rounding if parameter not given)
  * @returns {number} a float value representing the duration in the desired DurationUnit
  */
 convertTo(durationUnit, decimalNum) {
   const converted = this.#seconds / durationUnit.multiplier;
   if (decimalNum === undefined) {
     return converted;
   }
   return parseFloat(converted.toFixed(decimalNum));
 }
}

/**
* Enum for units of duration.
* @readonly
* @enum {{multiplier: number}}
*/
const DurationUnit = Object.freeze({
 SECOND: Object.freeze({multiplier: 1}),
 MINUTE: Object.freeze({multiplier: 60}),
 HOUR: Object.freeze({multiplier: 60 * 60}),
 DAY: Object.freeze({multiplier: 60 * 60 * 24}),
});



let d = Duration.of(1.33886, DurationUnit.DAY);
let converted = d.convertTo(DurationUnit.HOUR);
console.log(converted);
console.log(`${converted.toFixed(2)}`);
console.log(`${parseFloat(converted.toFixed(2))}`);
