package org.stairwaybook.scells

import java.awt.Color
import swing._
import event.{TableUpdated}

class Spreadsheet(val height: Int, val width: Int) extends ScrollPane {
  val cellModel = new Model(height, width)

  val table = new Table(height, width) {
    rowHeight = 25
    autoResizeMode = Table.AutoResizeMode.Off
    showGrid = true
    gridColor = new Color(150, 150, 150)

    def userData(row: Int, column: Int): String = {
      val v = this(row, column)
      if (v == null) "" else v.toString
    }

    override protected def rendererComponent(isSelected: Boolean, focused: Boolean, row: Int, column: Int) =
      if (focused) new TextField(userData(row, column))
      else
        new Label(cellModel.cells(row)(column).toString) {
          xAlignment = Alignment.Right
        }

    reactions += {
      case TableUpdated(table, rows, column) =>
        for (row <- rows)
          cellModel.cells(row)(column).formula = FormulaParsers.parse(userData(row, column))
      case cellModel.ValueChanged(cell) =>
        updateCell(cell.row, cell.column)
    }

    for (row <- cellModel.cells; cell <- row) listenTo(cell)
  }
  val rowHeader = new ListView((0 until  height) map (_.toString)) {
    fixedCellWidth = 30
    fixedCellHeight = table.rowHeight
  }
  viewportView = table
  rowHeaderView = rowHeader
}