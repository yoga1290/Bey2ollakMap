Android App for building statistics on how busy the roads are by generating Timepiece graphs on a Google Map.
Timepiece graphs of averages of vehicle speeds according to days & hours.


>	Please note, I'm just getting started… so, better UI & detailed code (w/out keys) later!


# Google App-Engine/NoSQL datastore:


Space-wise, fixed per 100-meter radius:

     Average= ( Average * Number_of_readings + New_Record) / (Number_of_readings+1)
     Number_of_readings = Number_of_readings + 1


Speed-wise, I'm doing a Binary search for retrieving data & w a separate node telling the order of their keys, insertion should be in log(n) as well..

![Binary search](readme/nosql.png)