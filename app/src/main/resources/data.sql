INSERT INTO users (id, role)
VALUES
  (1, 'PROFESSOR'),
  (2, 'STUDENT'),
  (3, 'STUDENT');

INSERT INTO profiles (id, user_id, email, first_name, last_name)
VALUES
  (1, 1, 'prof@demo.local',     'Иван',  'Петров'),
  (2, 2, 'student1@demo.local', 'Анна',  'Иванова'),
  (3, 3, 'student2@demo.local', 'Павел', 'Сидоров');

INSERT INTO course_categories (id, name)
VALUES (1, 'Программирование');

INSERT INTO courses (id, professor_id, category_id, title, description, start_date, due_date)
VALUES
  (1, 1, 1,
   'Основы Hibernate',
   'Введение в ORM и Hibernate на примере учебного проекта',
   '2025-01-10', '2025-02-28');

INSERT INTO modules (id, course_id, title, description, order_index)
VALUES
  (1, 1, 'Введение в ORM',
   'Понимание объектно-реляционного отображения и зачем оно нужно', 1),
  (2, 1, 'Основы Hibernate',
   'Сущности, аннотации, сессии и базовые маппинги', 2);

INSERT INTO lessons (id, module_id, title, content)
VALUES
  (1, 1, 'Зачем нужен ORM',
   'Текст урока про проблему object-relational mismatch и чем помогает ORM.'),
  (2, 1, 'ORM в Java мире',
   'Обзор JPA, Hibernate и альтернатив.'),
  (3, 2, 'Основные аннотации Hibernate',
   'Разбираем @Entity, @Id, @Table, @Column и базовую конфигурацию.'),
  (4, 2, 'Связи между сущностями',
   'Разбираем @OneToMany, @ManyToOne, @OneToOne, @ManyToMany.');

INSERT INTO assignments (id, lesson_id, title, description, due_date, max_score)
VALUES
  (1, 1,
   'Домашка 1: описать доменную модель',
   'Опиши доменную модель своего проекта в виде сущностей и связей.',
   '2025-01-15', 10),
  (2, 3,
   'Домашка 2: первая сущность',
   'Создай сущность User, таблицу users и свяжи их через JPA-аннотации.',
   '2025-01-20', 15);

INSERT INTO enrollments (id, student_id, course_id, enroll_date, status)
VALUES
  (1, 2, 1, '2025-01-05', 'ACTIVE');

INSERT INTO assignment_submissions (id, assignment_id, student_id, submitted_at, content, score, feedback)
VALUES
  (1, 1, 2,
   '2025-01-14 18:30:00',
   'Моя модель включает сущности User, Course, Enrollment и т.д.',
   9,
   'Хорошая работа, пара неточностей в связях.'),
  (2, 2, 2,
   '2025-01-19 21:10:00',
   'Создал сущность User, таблицу users и базовое маппирование.',
   NULL,
   NULL);

INSERT INTO quizzes (id, module_id, title)
VALUES
  (1, 2, 'Тест по основам Hibernate');

INSERT INTO quiz_questions (id, quiz_id, text, type)
VALUES
  (1, 1, 'Что такое ORM?', 'SINGLE_CHOICE'),
  (2, 1, 'Какие из перечисленных являются JPA/Hibernate-аннотациями?', 'MULTIPLE_CHOICE');

INSERT INTO quiz_answer_options (id, question_id, text, is_correct)
VALUES
  (1, 1, 'Object-Relational Mapping',        true),
  (2, 1, 'Only Relational Model',            false),
  (3, 2, '@Entity',                          true),
  (4, 2, '@Table',                           true),
  (5, 2, '@SpringBootApplication',           false);

INSERT INTO quiz_submissions (id, quiz_id, student_id, score, submitted_at)
VALUES
  (1, 1, 2, 2, '2025-01-21 20:00:00');
