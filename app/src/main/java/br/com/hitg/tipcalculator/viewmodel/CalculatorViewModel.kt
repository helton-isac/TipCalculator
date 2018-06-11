package br.com.hitg.tipcalculator.viewmodel

import android.app.Application
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.Transformations
import br.com.hitg.tipcalculator.R
import br.com.hitg.tipcalculator.model.Calculator
import br.com.hitg.tipcalculator.model.TipCalculation

class CalculatorViewModel @JvmOverloads constructor(app: Application,
                                                    val calculator: Calculator = Calculator()) :
        ObservableViewModel(app) {

    private var lastTipCalculated = TipCalculation()

    var inputCheckAmount = ""

    var inputTipPercentage = ""

    val outputCheckAmount
        get() = getApplication<Application>().getString(R.string.dollar_amount,
                lastTipCalculated.checkAmount)
    val outputTipAmount
        get() = getApplication<Application>().getString(R.string.dollar_amount,
                lastTipCalculated.tipAmount)
    val outputTotalDollarAmount
        get() = getApplication<Application>().getString(R.string.dollar_amount,
                lastTipCalculated.grandTotal)
    val locationName get() = lastTipCalculated.locationName


    init {
        updateOutputs(TipCalculation())
    }

    private fun updateOutputs(tc: TipCalculation) {
        lastTipCalculated = tc
        notifyChange()
    }

    fun calculateTip() {

        val checkAmount = inputCheckAmount.toDoubleOrNull()
        val tipPct = inputTipPercentage.toIntOrNull()

        if (checkAmount != null && tipPct != null) {
            updateOutputs(calculator.calculateTip(checkAmount, tipPct))
        }

    }

    fun saveCurrentTip(name: String) {
        val tipToSave = lastTipCalculated.copy(locationName = name)
        calculator.saveTipCalculation(tipToSave)
        updateOutputs(tipToSave)
    }

    fun loadSavedTipCalculationsSummaries(): LiveData<List<TipCalculationSummaryItem>> {
        return Transformations.map(calculator.loadSavedTipCalculations(), { tipCalculationObjects ->
            tipCalculationObjects.map {
                TipCalculationSummaryItem(it.locationName,
                        getApplication<Application>().getString(R.string.dollar_amount,
                                it.grandTotal))
            }

        })
    }

    fun loadTipCalculation(name: String) {
        val tc = calculator.loadTipCalculationByLocationName(name)
        if (tc != null) {
            inputCheckAmount = tc.checkAmount.toString()
            inputTipPercentage = tc.tipPct.toString()
            updateOutputs(tc)
            notifyChange()
        }
    }
}