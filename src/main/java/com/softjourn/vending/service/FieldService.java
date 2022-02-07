package com.softjourn.vending.service;

import com.softjourn.vending.dao.FieldRepository;
import com.softjourn.vending.dao.RowRepository;
import com.softjourn.vending.entity.Field;
import com.softjourn.vending.entity.Row;
import com.softjourn.vending.exceptions.AlreadyPresentedException;
import com.softjourn.vending.exceptions.NotFoundException;
import com.softjourn.vending.utils.ReflectionMergeUtil;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FieldService {

  private final FieldRepository fieldRepository;
  private final RowRepository rowRepository;
  private final ReflectionMergeUtil<Field> fieldMergeUtil;

  @Autowired
  public FieldService(FieldRepository fieldRepository, RowRepository rowRepository) {
    this.fieldRepository = fieldRepository;
    this.rowRepository = rowRepository;

    fieldMergeUtil = ReflectionMergeUtil
        .forClass(Field.class)
        .ignoreField("id")
        .ignoreField("position")
        .build();
  }

  public synchronized Field update(Integer id, Field field, Integer machineId) {
    Field old = fieldRepository.findById(id)
        .orElseThrow(() -> new NotFoundException(String.format("Field with id %d not found.", id)));
    checkNewInternalId(old, field.getInternalId(), machineId);
    Field newField = fieldMergeUtil.merge(old, field);
    return fieldRepository.save(newField);
  }

  public synchronized Row updateFieldsCountInRow(Integer id, Integer fieldsCount) {
    Row old = rowRepository.findById(id)
        .orElseThrow(() -> new NotFoundException(String.format("Row with id %d not found.", id)));
    if (old.getFields().size() == fieldsCount) return old;
    if (old.getFields().size() > fieldsCount) {
      removeRestFields(old, fieldsCount);
    } else {
      addRestFields(old, fieldsCount);
    }

    return rowRepository.save(old);
  }

  private void checkNewInternalId(Field old, String newId, Integer machineId) {
    if(old.getInternalId().equalsIgnoreCase(newId)) return;
    if (fieldRepository.checkIfThereIsSameInternalId(newId, machineId))
      throw new AlreadyPresentedException();
  }

  private void removeRestFields(Row row, Integer count) {
    List<Field> fields = row.getFields()
        .stream()
        .skip(count)
        .peek(fieldRepository::delete)
        .collect(Collectors.toList());
    row.removeFields(fields);
  }

  @SuppressWarnings("unchecked")
  private void addRestFields(Row row, Integer count) {
    String lastNumber = row
        .getFields()
        .get(row.getFields().size() - 1)
        .getInternalId()
        .replace(row.getRowId(), "");

    Stream stream;

    if(lastNumber.matches("\\d+")) {
      Integer n = Integer.parseInt(lastNumber);
      stream = Stream.iterate(n + 1, x -> x + 1);
    } else {
      char n = lastNumber.charAt(0);
      stream = Stream.iterate((char)(n + 1), x -> (char)(x + 1));
    }

    stream.limit(count - row.getFields().size())
        .map(x -> row.getRowId() + x)
        .map(id-> new Field((String)id, row.getFields().size()))
        .forEach(f -> {
          fieldRepository.save((Field) f);
          row.adField((Field)f);
        });

  }
}
