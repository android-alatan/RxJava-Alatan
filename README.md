[![Main Commit](https://github.com/android-alatan/RxJava-Alatan/actions/workflows/lib-main-branch.yml/badge.svg?branch=main)](https://github.com/android-alatan/RxJava-Alatan/actions/workflows/lib-main-branch.yml)
[![Release](https://jitpack.io/v/android-alatan/rxjava-alatan.svg)](https://jitpack.io/#android-alatan/rxjava-alatan)

# RxJava Alatan

This repo is group of RxJava utils.

# RxJava Scheduler Api & Assertion
```kotlin
implementation("com.github.android-alatan.rxjava-alatan:rx-scheduler-api:$version")

testImplementation("com.github.android-alatan.rxjava-alatan:rx-scheduler-assertion:$version")
```
In many cases, devs declare RxJava stream's thread. Devs can use Schedulers directly. But it may cause inconvenient in test case.

above 2 interface, It helps to inject intended thread in production code and test code

## How to use
```kotlin
// in production code
val schedulerProvider = SchedulerProvider()
dataFetcher.getData()
  .subscribeOn(schedulerProvider.io())
  .observeOn(schedulerProvider.ui())
  .subscribe { /* do something */ }

// in test code
val schedulerProvider = MockSchedulerProvider().immediate()
dataFetcher.getData()
  .subscribeOn(schedulerProvider.io()) // on trampoline thread
  .observeOn(schedulerProvider.ui()) // on trampoline thread
  .subscribe { /* do something */ }

// in test code but async
val schedulerProvider = MockSchedulerProvider().async()
dataFetcher.getData()
  .delay(3000L, schedulerProvider.computation()) // on test scheduler
  .subscribe { /* do something */ }

schedulerProvider.advanceTimeBy(3000L, TimeUnit.MILLISECONDS)
schedulerProvider.triggerActions() // will trigger data emission.
```

# RxJava RecyclerView Adapter
```kotlin
implementation("com.github.android-alatan.rxjava-alatan:rx-recyclerview-adapter:$version")
```
This is to help RecyclerView Adapter combine with RxJava to add data.

## How to use
```kotlin
val adapter = DiffUtilAdapter<Data>()

api.getData()
  .compose(adapter.asRxTransformer())
  .subscribe()
```
by `compose(adapter.asRxTransformer())`, Adapter will be bound data with DiffUtil api.