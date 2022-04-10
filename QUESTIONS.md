# Questions
* What technical/business constraints should the data storage component of the
  program architecture meet to fulfill the requirement described by the
  customer in paragraph «Statistics» ?  So what kind of component(s) (listed in
  the lecture) will the architecture need?

Since the reports are JSON files, the preferred model would be a No-SQL
database that you be fast for writing. The 2 constraints chosen for this
database would be availability and partition tolerance because a new report
will be added into the database frequently, and we need to have access to the
database all the time. But since the statistics are not accessed every seconds,
we can allow consistency errors, and settle for a DB that is eventually
consistent. We will therefore need a distributed No-SQL database.

* What business constraint should the architecture meet to fulfill the
  requirement describe in the paragraph «Alert»? Which component to choose?

We need the peacewatchers to be fast and not be slowed down by the process of
managing the reports. We need the alerts to be sent fast to the peacemaker.
For that we need distributed streams between the peacewatchers and processes
and between the processes and peacemakers. We also need to send the reports to
the database, for that we also need a stream to not slow down the processes.

* What mistake(s) from Peaceland can explain the failed attempt?

Datascientists didn't take into account the size of the problem. They likely
didn't expect this much data to store for the statistics and probably didn't
use a micro-service for the processes handling the alerts, and only had one
server doing the job, making it impossible to scale.

* Peaceland has likely forgotten some technical information in the report sent
  by the drone. In the future, this information could help Peaceland make its
  peacewatchers much more efficient. Which information?

TODO
