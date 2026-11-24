SELECT id, complaint_number, branch_id
FROM complaints
WHERE id = 5;


SELECT id, name, code
FROM branches;

UPDATE users
SET branch_id = 1
WHERE id = 3;

SELECT id, name, branch_id, role
FROM users
WHERE id = 3;


SELECT id, complaint_number, status, branch_id
FROM complaints
WHERE id = 5;
show tables


DESCRIBE complaints;

SELECT
    id,
    complaint_number,
    branch_id,
    assigned_manager_id,
    assigned_technician_id,
    status
FROM complaints
WHERE id = 5;
